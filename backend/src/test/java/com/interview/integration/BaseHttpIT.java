package com.interview.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public abstract class BaseHttpIT {

    @LocalServerPort
    protected int port;

    @Value("${test.api.base-url-template}")
    protected String baseUrlTemplate;

    protected String baseUrl;

    @BeforeEach
    void setUpBaseUrl() {
        baseUrl = baseUrlTemplate.formatted(port);
    }
}
