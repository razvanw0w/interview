package com.interview.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableMethodSecurity
public class WebMvcTestSecurityConfig {

    private static final String HEALTH_ENDPOINT = "/actuator/health";
    private static final String PROMETHEUS_ENDPOINT = "/actuator/prometheus";
    private static final String HEALTH_ENDPOINT_ALL = "/actuator/health/**";

    private static final String AUTHORS_API = "/authors/**";
    private static final String BOOKS_API = "/books/**";

    private static final String ROLES_CLAIM = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HEALTH_ENDPOINT, HEALTH_ENDPOINT_ALL, PROMETHEUS_ENDPOINT).permitAll();
                    configureCrudAccess(auth, AUTHORS_API);
                    configureCrudAccess(auth, BOOKS_API);
                    auth.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                );

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName(ROLES_CLAIM);
        authoritiesConverter.setAuthorityPrefix(ROLE_PREFIX);

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }

    private void configureCrudAccess(
            org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
            String pattern
    ) {
        auth.requestMatchers(HttpMethod.GET, pattern).hasAnyRole("USER", "ADMIN");
        auth.requestMatchers(HttpMethod.POST, pattern).hasRole("ADMIN");
        auth.requestMatchers(HttpMethod.PUT, pattern).hasRole("ADMIN");
        auth.requestMatchers(HttpMethod.DELETE, pattern).hasRole("ADMIN");
    }
}