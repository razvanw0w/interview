package com.interview.integration;

import com.interview.config.IntegrationTestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/reset-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Import(IntegrationTestSecurityConfig.class)
public abstract class BaseHttpIT {

    protected static final String USER_TOKEN = "user-token";
    protected static final String ADMIN_TOKEN = "admin-token";

    @LocalServerPort
    protected int port;

    @Value("${test.api.base-url-template}")
    protected String baseUrlTemplate;

    protected String baseUrl;

    @BeforeEach
    void setUpBaseUrl() {
        baseUrl = baseUrlTemplate.formatted(port);
    }

    protected String url(String path) {
        return baseUrl + path;
    }

    protected HttpHeaders userHeaders() {
        return bearerHeaders(USER_TOKEN);
    }

    protected HttpHeaders adminHeaders() {
        return bearerHeaders(ADMIN_TOKEN);
    }

    protected HttpEntity<Void> userEntity() {
        return new HttpEntity<>(userHeaders());
    }

    protected <T> HttpEntity<T> userEntity(T body) {
        return new HttpEntity<>(body, userHeaders());
    }

    protected HttpEntity<Void> adminEntity() {
        return new HttpEntity<>(adminHeaders());
    }

    protected <T> HttpEntity<T> adminEntity(T body) {
        return new HttpEntity<>(body, adminHeaders());
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}