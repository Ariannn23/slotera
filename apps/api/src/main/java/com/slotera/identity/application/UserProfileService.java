package com.slotera.identity.application;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserProfileService {

    private final JdbcTemplate jdbc;

    public UserProfileService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public UserProfile findOrCreate(Jwt jwt) {
        Object emailClaim = jwt.getClaim("email");
        if (!(emailClaim instanceof String email) || !Boolean.TRUE.equals(jwt.getClaim("email_verified")) || !StringUtils.hasText(email)
                || email.length() > 320) {
            throw new AccessDeniedException("El correo debe estar verificado.");
        }
        String issuer = jwt.getIssuer().toString();
        String subject = jwt.getSubject();
        List<UserProfile> users = jdbc.query("""
                INSERT INTO users (id, issuer, subject, email, email_verified)
                VALUES (?, ?, ?, ?, TRUE)
                ON CONFLICT (issuer, subject) DO UPDATE
                  SET email = EXCLUDED.email,
                      email_verified = TRUE,
                      updated_at = CASE WHEN users.email IS DISTINCT FROM EXCLUDED.email
                          OR NOT users.email_verified THEN CURRENT_TIMESTAMP ELSE users.updated_at END
                  WHERE users.status = 'ACTIVE'
                RETURNING id, email, email_verified
                """, (row, index) -> new UserProfile(row.getObject("id", UUID.class),
                row.getString("email"), row.getBoolean("email_verified")), UUID.randomUUID(), issuer, subject, email);
        if (users.isEmpty()) {
            throw new AccessDeniedException("La cuenta no está activa.");
        }
        return users.getFirst();
    }

    public record UserProfile(UUID id, String email, boolean emailVerified) { }
}
