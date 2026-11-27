package com.slotera.identity.security;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityProblems problems) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(errors -> errors.authenticationEntryPoint(problems::unauthenticated)
                        .accessDeniedHandler(problems::forbidden))
                .oauth2ResourceServer(oauth -> oauth.authenticationEntryPoint(problems::unauthenticated)
                        .accessDeniedHandler(problems::forbidden).jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${slotera.security.issuer}") String issuer,
                          @Value("${slotera.security.audience}") String audience,
                          Environment environment) {
        URI issuerUri = validateUrl(issuer, environment, false);
        if (!StringUtils.hasText(audience) || audience.contains("${")) {
            throw new IllegalStateException("La audiencia OIDC debe configurarse.");
        }
        if (issuer.length() > 255 || issuer.endsWith("/")) {
            throw new IllegalStateException("El emisor OIDC debe tener como máximo 255 caracteres y no terminar en barra.");
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(issuerUri + "/protocol/openid-connect/certs")
                .jwsAlgorithm(SignatureAlgorithm.RS256).build();
        OAuth2TokenValidator<Jwt> requiredClaims = jwt -> {
            List<String> audiences = jwt.getAudience();
            String subject = jwt.getSubject();
            if (audiences == null || !audiences.contains(audience) || jwt.getExpiresAt() == null
                    || !"Bearer".equals(jwt.getClaimAsString("typ"))
                    || !StringUtils.hasText(subject) || subject.length() > 255) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
            }
            return OAuth2TokenValidatorResult.success();
        };
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer), requiredClaims));
        return decoder;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${slotera.security.web-origin}") String origin,
                                                     Environment environment) {
        validateUrl(origin, environment, true);
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(origin));
        configuration.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private URI validateUrl(String value, Environment environment, boolean origin) {
        URI uri;
        try {
            uri = URI.create(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("La configuración OIDC o del origen web no es válida.");
        }
        boolean validScheme = "http".equals(uri.getScheme()) || "https".equals(uri.getScheme());
        boolean production = environment.acceptsProfiles(Profiles.of("prod"));
        if (!validScheme || uri.getHost() == null || uri.getUserInfo() != null || uri.getQuery() != null
                || uri.getFragment() != null || (production && !"https".equals(uri.getScheme()))
                || (origin && uri.getPath() != null && !uri.getPath().isEmpty())) {
            throw new IllegalStateException("La configuración OIDC o del origen web no es válida.");
        }
        return uri;
    }
}
