package com.interview.integration;

import com.interview.dto.BookRequest;
import com.interview.dto.BookResponse;
import com.interview.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/reset-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookResourceIT extends BaseHttpIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldGetAllBooks() {
        ResponseEntity<BookResponse[]> response =
                restTemplate.getForEntity(baseUrl + "/books", BookResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(5);
    }

    @Test
    void shouldGetBookById() {
        ResponseEntity<BookResponse> response =
                restTemplate.getForEntity(baseUrl + "/books/1", BookResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().title()).isEqualTo("Effective Java");
        assertThat(response.getBody().isbn()).isEqualTo("9780134685991");
        assertThat(response.getBody().publishedYear()).isEqualTo(2018);
        assertThat(response.getBody().authorId()).isEqualTo(1L);
        assertThat(response.getBody().authorName()).isEqualTo("Joshua Bloch");
    }

    @Test
    void shouldCreateBook() {
        BookRequest request = new BookRequest(
                "Domain-Driven Design",
                "9780321125217",
                2003,
                1L
        );

        ResponseEntity<BookResponse> response =
                restTemplate.postForEntity(baseUrl + "/books", request, BookResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Domain-Driven Design");
        assertThat(response.getBody().isbn()).isEqualTo("9780321125217");
        assertThat(response.getBody().publishedYear()).isEqualTo(2003);
        assertThat(response.getBody().authorId()).isEqualTo(1L);
        assertThat(response.getBody().authorName()).isEqualTo("Joshua Bloch");
    }

    @Test
    void shouldUpdateBook() {
        BookRequest request = new BookRequest(
                "Effective Java 3rd Edition",
                "9780134685991",
                2018,
                1L
        );

        HttpEntity<BookRequest> entity = new HttpEntity<>(request);

        ResponseEntity<BookResponse> response =
                restTemplate.exchange(baseUrl + "/books/1", PUT, entity, BookResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().title()).isEqualTo("Effective Java 3rd Edition");
        assertThat(response.getBody().authorId()).isEqualTo(1L);
        assertThat(response.getBody().authorName()).isEqualTo("Joshua Bloch");
    }

    @Test
    void shouldDeleteBook() {
        ResponseEntity<Void> deleteResponse =
                restTemplate.exchange(baseUrl + "/books/5", DELETE, null, Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponse> getResponse =
                restTemplate.getForEntity(baseUrl + "/books/5", ErrorResponse.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().error()).isEqualTo("Book not found with id 5");
        assertThat(getResponse.getBody().status()).isEqualTo(404);
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() {
        ResponseEntity<ErrorResponse> response =
                restTemplate.getForEntity(baseUrl + "/books/999999", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Book not found with id 999999");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingBook() {
        BookRequest request = new BookRequest(
                "Missing Book",
                "9999999999999",
                2024,
                1L
        );

        HttpEntity<BookRequest> entity = new HttpEntity<>(request);

        ResponseEntity<ErrorResponse> response =
                restTemplate.exchange(baseUrl + "/books/999999", PUT, entity, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Book not found with id 999999");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingBook() {
        ResponseEntity<ErrorResponse> response =
                restTemplate.exchange(baseUrl + "/books/999999", DELETE, null, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Book not found with id 999999");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingBookWithMissingAuthor() {
        BookRequest request = new BookRequest(
                "New Book",
                "8888888888888",
                2024,
                999999L
        );

        ResponseEntity<ErrorResponse> response =
                restTemplate.postForEntity(baseUrl + "/books", request, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Author not found with id 999999");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingBookWithMissingAuthor() {
        BookRequest request = new BookRequest(
                "Effective Java Updated",
                "9780134685991",
                2018,
                999999L
        );

        HttpEntity<BookRequest> entity = new HttpEntity<>(request);

        ResponseEntity<ErrorResponse> response =
                restTemplate.exchange(baseUrl + "/books/1", PUT, entity, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Author not found with id 999999");
        assertThat(response.getBody().status()).isEqualTo(404);
    }
}
