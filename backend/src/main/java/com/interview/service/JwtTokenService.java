package com.interview.service;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenService {

    private final PrivateKey privateKey;
    private final long expirationSeconds;

    public JwtTokenService(
            @Value("${security.jwt.private-key-location}") Resource privateKeyResource,
            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds
    ) throws Exception {
        this.privateKey = loadPrivateKey(privateKeyResource);
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(String username, List<String> roles) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private PrivateKey loadPrivateKey(Resource resource) throws Exception {
        String pem = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
}
