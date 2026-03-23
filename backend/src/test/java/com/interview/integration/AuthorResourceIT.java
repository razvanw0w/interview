package com.interview.integration;

import com.interview.dto.AuthorRequest;
import com.interview.dto.AuthorResponse;
import com.interview.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/reset-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthorResourceIT extends BaseHttpIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldGetAllAuthors() {
        ResponseEntity<AuthorResponse[]> response =
                restTemplate.getForEntity(baseUrl + "/authors", AuthorResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(3);
    }

    @Test
    void shouldGetAuthorById() {
        ResponseEntity<AuthorResponse> response =
                restTemplate.getForEntity(baseUrl + "/authors/1", AuthorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().name()).contains("Joshua Bloch");
        assertThat(response.getBody().email()).isEqualTo("joshua.bloch@example.com");
    }

    @Test
    void shouldCreateAuthor() {
        AuthorRequest request = new AuthorRequest(
                "Kent Beck",
                "kent.beck@example.com"
        );

        ResponseEntity<AuthorResponse> response =
                restTemplate.postForEntity(baseUrl + "/authors", request, AuthorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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
                "joshua.bloch@example.com"
        );

        HttpEntity<AuthorRequest> entity = new HttpEntity<>(request);

        ResponseEntity<AuthorResponse> response =
                restTemplate.exchange(baseUrl + "/authors/1", PUT, entity, AuthorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().name()).isEqualTo("Joshua Bloch Updated");
        assertThat(response.getBody().email()).isEqualTo("joshua.bloch@example.com");
    }

    @Test
    void shouldDeleteAuthor() {
        ResponseEntity<Void> deleteResponse =
                restTemplate.exchange(baseUrl + "/authors/3", DELETE, null, Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponse> getResponse =
                restTemplate.getForEntity(baseUrl + "/authors/3", ErrorResponse.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().error()).isEqualTo("Author not found with id 3");
        assertThat(getResponse.getBody().status()).isEqualTo(404);
    }

    @Test
    void shouldReturnNotFoundWhenAuthorDoesNotExist() {
        ResponseEntity<ErrorResponse> response =
                restTemplate.getForEntity(baseUrl + "/authors/999999", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Author not found with id 999999");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingAuthor() {
        AuthorRequest request = new AuthorRequest(
                "Missing Author",
                "missing@example.com"
        );

        HttpEntity<AuthorRequest> entity = new HttpEntity<>(request);

        ResponseEntity<ErrorResponse> response =
                restTemplate.exchange(baseUrl + "/authors/999999", PUT, entity, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Author not found with id 999999");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingAuthor() {
        ResponseEntity<ErrorResponse> response =
                restTemplate.exchange(baseUrl + "/authors/999999", DELETE, null, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Author not found with id 999999");
        assertThat(response.getBody().status()).isEqualTo(404);
    }
}
