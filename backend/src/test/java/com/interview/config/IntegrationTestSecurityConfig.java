package com.interview.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.List;

@TestConfiguration
public class IntegrationTestSecurityConfig {

    public static final String USER_TOKEN = "user-token";
    public static final String ADMIN_TOKEN = "admin-token";

    private static final String TEST_SUBJECT = "test-user";
    private static final long EXPIRES_IN_SECONDS = 3600L;

    @Bean
    JwtDecoder jwtDecoder() {
        return token -> switch (token) {
            case USER_TOKEN -> buildJwt(token, List.of("USER"));
            case ADMIN_TOKEN -> buildJwt(token, List.of("ADMIN"));
            default -> throw new IllegalArgumentException("Unknown test token: " + token);
        };
    }

    private Jwt buildJwt(String token, List<String> roles) {
        Instant now = Instant.now();

        return Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("sub", TEST_SUBJECT)
                .claim("roles", roles)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRES_IN_SECONDS))
                .build();
    }
}