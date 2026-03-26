package com.interview.integration;

import com.interview.dto.AuthorRequest;
import com.interview.dto.AuthorResponse;
import com.interview.dto.ErrorResponse;
import com.interview.dto.RestPagedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

class AuthorResourceIT extends BaseHttpIT {

    private static final long EXISTING_AUTHOR_ID = 1L;
    private static final long DELETABLE_AUTHOR_ID = 3L;
    private static final long MISSING_AUTHOR_ID = 999999L;

    private static final String EXISTING_AUTHOR_NAME = "Joshua Bloch";
    private static final String EXISTING_AUTHOR_EMAIL = "joshua.bloch@example.com";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldGetAllAuthors() {
        ResponseEntity<RestPagedResponse<AuthorResponse>> response = restTemplate.exchange(
                url("/authors?page=0&size=10&sort=id,asc"),
                GET,
                userEntity(),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSizeGreaterThanOrEqualTo(3);
        assertThat(response.getBody().page().totalElements()).isGreaterThanOrEqualTo(3);
        assertThat(response.getBody().page().number()).isEqualTo(0);
        assertThat(response.getBody().page().size()).isEqualTo(10);
    }

    @Test
    void shouldGetAuthorById() {
        ResponseEntity<AuthorResponse> response = restTemplate.exchange(
                url("/authors/" + EXISTING_AUTHOR_ID),
                GET,
                userEntity(),
                AuthorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(EXISTING_AUTHOR_ID);
        assertThat(response.getBody().name()).contains(EXISTING_AUTHOR_NAME);
        assertThat(response.getBody().email()).isEqualTo(EXISTING_AUTHOR_EMAIL);
    }

    @Test
    void shouldCreateAuthor() {
        AuthorRequest request = new AuthorRequest(
                "Kent Beck",
                "kent.beck@example.com"
        );

        ResponseEntity<AuthorResponse> response = restTemplate.exchange(
                url("/authors"),
                POST,
                adminEntity(request),
                AuthorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Kent Beck");
        assertThat(response.getBody().email()).isEqualTo("kent.beck@example.com");
        assertThat(response.getBody().books()).isEmpty();
    }

    @Test
    void shouldUpdateAuthor() {
        AuthorRequest request = new AuthorRequest(
                "Joshua Bloch Updated",
                EXISTING_AUTHOR_EMAIL
        );

        ResponseEntity<AuthorResponse> response = restTemplate.exchange(
                url("/authors/" + EXISTING_AUTHOR_ID),
                PUT,
                adminEntity(request),
                AuthorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(EXISTING_AUTHOR_ID);
        assertThat(response.getBody().name()).isEqualTo("Joshua Bloch Updated");
        assertThat(response.getBody().email()).isEqualTo(EXISTING_AUTHOR_EMAIL);
    }

    @Test
    void shouldDeleteAuthor() {
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/authors/" + DELETABLE_AUTHOR_ID),
                DELETE,
                adminEntity(),
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(NO_CONTENT);

        ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(
                url("/authors/" + DELETABLE_AUTHOR_ID),
                GET,
                userEntity(),
                ErrorResponse.class
        );

        assertNotFound(getResponse, notFoundMessage(DELETABLE_AUTHOR_ID));
    }

    @Test
    void shouldReturnNotFoundWhenAuthorDoesNotExist() {
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url("/authors/" + MISSING_AUTHOR_ID),
                GET,
                userEntity(),
                ErrorResponse.class
        );

        assertNotFound(response, notFoundMessage(MISSING_AUTHOR_ID));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingAuthor() {
        AuthorRequest request = new AuthorRequest(
                "Missing Author",
                "missing@example.com"
        );

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url("/authors/" + MISSING_AUTHOR_ID),
                PUT,
                adminEntity(request),
                ErrorResponse.class
        );

        assertNotFound(response, notFoundMessage(MISSING_AUTHOR_ID));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingAuthor() {
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url("/authors/" + MISSING_AUTHOR_ID),
                DELETE,
                adminEntity(),
                ErrorResponse.class
        );

        assertNotFound(response, notFoundMessage(MISSING_AUTHOR_ID));
    }

    private void assertNotFound(ResponseEntity<ErrorResponse> response, String expectedMessage) {
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(expectedMessage);
        assertThat(response.getBody().status()).isEqualTo(NOT_FOUND.value());
    }

    private String notFoundMessage(long authorId) {
        return "Author not found with id " + authorId;
    }
}