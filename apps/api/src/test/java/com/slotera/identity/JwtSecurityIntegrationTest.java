package com.slotera.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtSecurityIntegrationTest {

    private static final RSAKey SIGNING_KEY;
    private static final RSAKey WRONG_KEY;
    private static final HttpServer JWKS_SERVER;
    private static final String ISSUER;

    static {
        try {
            SIGNING_KEY = new RSAKeyGenerator(2048).keyID("slotera-test-key").generate();
            WRONG_KEY = new RSAKeyGenerator(2048).keyID("slotera-test-key").generate();
            JWKS_SERVER = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            JWKS_SERVER.createContext("/realms/test/protocol/openid-connect/certs", exchange -> {
                byte[] body = new JWKSet(SIGNING_KEY.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                try (var output = exchange.getResponseBody()) {
                    output.write(body);
                }
            });
            JWKS_SERVER.start();
            ISSUER = "http://127.0.0.1:" + JWKS_SERVER.getAddress().getPort() + "/realms/test";
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @DynamicPropertySource
    static void configureIssuer(DynamicPropertyRegistry properties) {
        properties.add("slotera.security.issuer", () -> ISSUER);
        properties.add("slotera.security.audience", () -> "slotera-api");
    }

    @AfterAll
    static void stopJwksServer() {
        JWKS_SERVER.stop(0);
    }

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired TransactionTemplate transactions;

    @Test
    void rejectsMissingOrInvalidBearerTokens() throws Exception {
        mvc.perform(get("/api/v1/me"))
                .andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer malformed"))
                .andExpect(status().isUnauthorized());

        Instant now = Instant.now();
        List<String> invalidTokens = List.of(
                token("wrong-signature", ISSUER, "slotera-api", now.plusSeconds(300), now.minusSeconds(1), true, WRONG_KEY),
                token("wrong-issuer", ISSUER + "-other", "slotera-api", now.plusSeconds(300), now.minusSeconds(1), true, SIGNING_KEY),
                token("wrong-audience", ISSUER, "other-api", now.plusSeconds(300), now.minusSeconds(1), true, SIGNING_KEY),
                token("id-token", ISSUER, "slotera-web", now.plusSeconds(300), now.minusSeconds(1), true, SIGNING_KEY),
                token("id-token-wrong-type", ISSUER, "slotera-api", now.plusSeconds(300), now.minusSeconds(1), true, SIGNING_KEY, "ID"),
                token("expired", ISSUER, "slotera-api", now.minusSeconds(120), now.minusSeconds(300), true, SIGNING_KEY),
                token("future", ISSUER, "slotera-api", now.plusSeconds(300), now.plusSeconds(120), true, SIGNING_KEY),
                token("missing-exp", ISSUER, "slotera-api", null, now.minusSeconds(1), true, SIGNING_KEY),
                token(null, ISSUER, "slotera-api", now.plusSeconds(300), now.minusSeconds(1), true, SIGNING_KEY)
        );
        for (String invalid : invalidTokens) {
            mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + invalid))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
        }
    }

    @Test
    void allowsOnlyVerifiedActiveIdentityWithoutOrganizationHeader() throws Exception {
        String subject = "verified-" + UUID.randomUUID();
        String accessToken = token(subject, ISSUER, "slotera-api", Instant.now().plusSeconds(300),
                Instant.now().minusSeconds(1), true, SIGNING_KEY);
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("persona@example.test"))
                .andExpect(jsonPath("$.emailVerified").value(true))
                .andExpect(jsonPath("$.subject").doesNotExist());
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + accessToken)
                        .header("X-Organization-Id", "empresa-ajena"))
                .andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM users WHERE issuer = ? AND subject = ?",
                Integer.class, ISSUER, subject)).isEqualTo(1);

        String unverified = token("unverified-" + UUID.randomUUID(), ISSUER, "slotera-api", Instant.now().plusSeconds(300),
                Instant.now().minusSeconds(1), false, SIGNING_KEY);
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + unverified))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("FORBIDDEN"));

        JWTClaimsSet incompleteClaims = new JWTClaimsSet.Builder().issuer(ISSUER)
                .subject("missing-email-" + UUID.randomUUID()).audience("slotera-api")
                .expirationTime(Date.from(Instant.now().plusSeconds(300))).claim("typ", "Bearer").build();
        SignedJWT incompleteToken = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(SIGNING_KEY.getKeyID()).build(), incompleteClaims);
        incompleteToken.sign(new RSASSASigner(SIGNING_KEY));
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + incompleteToken.serialize()))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("FORBIDDEN"));

        jdbc.update("UPDATE users SET status = 'INACTIVE' WHERE issuer = ? AND subject = ?", ISSUER, subject);
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void simultaneousFirstRequestsCreateOneUser() throws Exception {
        String subject = "parallel-" + UUID.randomUUID();
        String accessToken = token(subject, ISSUER, "slotera-api", Instant.now().plusSeconds(300),
                Instant.now().minusSeconds(1), true, SIGNING_KEY);
        CyclicBarrier barrier = new CyclicBarrier(2);
        java.util.function.Supplier<Integer> request = () -> {
            try {
                barrier.await();
                return mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + accessToken))
                        .andReturn().getResponse().getStatus();
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        };
        CompletableFuture<Integer> first = CompletableFuture.supplyAsync(request);
        CompletableFuture<Integer> second = CompletableFuture.supplyAsync(request);
        assertThat(List.of(first.get(), second.get())).containsExactly(200, 200);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM users WHERE issuer = ? AND subject = ?",
                Integer.class, ISSUER, subject)).isEqualTo(1);
    }

    @Test
    void doesNotMergeDifferentSubjectsWithSameEmailOrOverwriteWithUnverifiedEmail() throws Exception {
        String firstSubject = "same-mail-a-" + UUID.randomUUID();
        String secondSubject = "same-mail-b-" + UUID.randomUUID();
        Instant expiration = Instant.now().plusSeconds(300);
        Instant notBefore = Instant.now().minusSeconds(1);
        String first = token(firstSubject, ISSUER, "slotera-api", expiration, notBefore, true, SIGNING_KEY);
        String second = token(secondSubject, ISSUER, "slotera-api", expiration, notBefore, true, SIGNING_KEY);
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + first)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + second)).andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT count(DISTINCT id) FROM users WHERE issuer = ? AND subject IN (?, ?)",
                Integer.class, ISSUER, firstSubject, secondSubject)).isEqualTo(2);

        String unverified = token(firstSubject, ISSUER, "slotera-api", expiration, notBefore, false, SIGNING_KEY);
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + unverified))
                .andExpect(status().isForbidden());
        assertThat(jdbc.queryForObject("SELECT email_verified FROM users WHERE issuer = ? AND subject = ?",
                Boolean.class, ISSUER, firstSubject)).isTrue();
    }

    @Test
    void failedTransactionDoesNotLeavePartialProfile() throws Exception {
        String subject = "rollback-" + UUID.randomUUID();
        String accessToken = token(subject, ISSUER, "slotera-api", Instant.now().plusSeconds(300),
                Instant.now().minusSeconds(1), true, SIGNING_KEY);
        transactions.executeWithoutResult(transaction -> {
            try {
                mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + accessToken))
                        .andExpect(status().isOk());
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
            transaction.setRollbackOnly();
        });
        assertThat(jdbc.queryForObject("SELECT count(*) FROM users WHERE issuer = ? AND subject = ?",
                Integer.class, ISSUER, subject)).isZero();
    }

    private static String token(String subject, String issuer, String audience, Instant expiration,
                                Instant notBefore, boolean emailVerified, RSAKey key) throws Exception {
        return token(subject, issuer, audience, expiration, notBefore, emailVerified, key, "Bearer");
    }

    private static String token(String subject, String issuer, String audience, Instant expiration,
                                Instant notBefore, boolean emailVerified, RSAKey key, String type) throws Exception {
        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder().issuer(issuer).subject(subject)
                .audience(audience).issueTime(new Date()).notBeforeTime(Date.from(notBefore)).claim("typ", type)
                .claim("email", "persona@example.test").claim("email_verified", emailVerified);
        if (expiration != null) {
            claims.expirationTime(Date.from(expiration));
        }
        SignedJWT signed = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), claims.build());
        signed.sign(new RSASSASigner(key));
        return signed.serialize();
    }
}
