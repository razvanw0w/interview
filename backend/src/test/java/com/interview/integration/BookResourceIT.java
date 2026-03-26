package com.interview.integration;

import com.interview.dto.BookRequest;
import com.interview.dto.BookResponse;
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

class BookResourceIT extends BaseHttpIT {

    private static final long EXISTING_BOOK_ID = 1L;
    private static final long DELETABLE_BOOK_ID = 5L;
    private static final long EXISTING_AUTHOR_ID = 1L;
    private static final long MISSING_BOOK_ID = 999999L;
    private static final long MISSING_AUTHOR_ID = 999999L;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldGetAllBooks() {
        ResponseEntity<RestPagedResponse<BookResponse>> response = restTemplate.exchange(
                url("/books?page=0&size=10&sort=id,asc"),
                GET,
                userEntity(),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSizeGreaterThanOrEqualTo(5);
        assertThat(response.getBody().page().totalElements()).isGreaterThanOrEqualTo(5);
        assertThat(response.getBody().page().number()).isEqualTo(0);
        assertThat(response.getBody().page().size()).isEqualTo(10);
    }

    @Test
    void shouldGetBookById() {
        ResponseEntity<BookResponse> response = restTemplate.exchange(
                url("/books/" + EXISTING_BOOK_ID),
                GET,
                userEntity(),
                BookResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(EXISTING_BOOK_ID);
        assertThat(response.getBody().title()).isEqualTo("Effective Java");
        assertThat(response.getBody().isbn()).isEqualTo("9780134685991");
        assertThat(response.getBody().publishedYear()).isEqualTo(2018);
        assertThat(response.getBody().authorId()).isEqualTo(EXISTING_AUTHOR_ID);
        assertThat(response.getBody().authorName()).isEqualTo("Joshua Bloch");
    }

    @Test
    void shouldCreateBook() {
        BookRequest request = new BookRequest(
                "Domain-Driven Design",
                "9780321125217",
                2003,
                EXISTING_AUTHOR_ID
        );

        ResponseEntity<BookResponse> response = restTemplate.exchange(
                url("/books"),
                POST,
                adminEntity(request),
                BookResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Domain-Driven Design");
        assertThat(response.getBody().isbn()).isEqualTo("9780321125217");
        assertThat(response.getBody().publishedYear()).isEqualTo(2003);
        assertThat(response.getBody().authorId()).isEqualTo(EXISTING_AUTHOR_ID);
        assertThat(response.getBody().authorName()).isEqualTo("Joshua Bloch");
    }

    @Test
    void shouldUpdateBook() {
        BookRequest request = new BookRequest(
                "Effective Java 3rd Edition",
                "9780134685991",
                2018,
                EXISTING_AUTHOR_ID
        );

        ResponseEntity<BookResponse> response = restTemplate.exchange(
                url("/books/" + EXISTING_BOOK_ID),
                PUT,
                adminEntity(request),
                BookResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(EXISTING_BOOK_ID);
        assertThat(response.getBody().title()).isEqualTo("Effective Java 3rd Edition");
        assertThat(response.getBody().authorId()).isEqualTo(EXISTING_AUTHOR_ID);
        assertThat(response.getBody().authorName()).isEqualTo("Joshua Bloch");
    }

    @Test
    void shouldDeleteBook() {
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/books/" + DELETABLE_BOOK_ID),
                DELETE,
                adminEntity(),
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(NO_CONTENT);

        ResponseEntity<ErrorResponse> getResponse = restTemplate.exchange(
                url("/books/" + DELETABLE_BOOK_ID),
                GET,
                userEntity(),
                ErrorResponse.class
        );

        assertNotFound(getResponse, bookNotFoundMessage(DELETABLE_BOOK_ID));
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() {
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url("/books/" + MISSING_BOOK_ID),
                GET,
                userEntity(),
                ErrorResponse.class
        );

        assertNotFound(response, bookNotFoundMessage(MISSING_BOOK_ID));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingBook() {
        BookRequest request = new BookRequest(
                "Missing Book",
                "9999999999999",
                2024,
                EXISTING_AUTHOR_ID
        );

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url("/books/" + MISSING_BOOK_ID),
                PUT,
                adminEntity(request),
                ErrorResponse.class
        );

        assertNotFound(response, bookNotFoundMessage(MISSING_BOOK_ID));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingBook() {
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url("/books/" + MISSING_BOOK_ID),
                DELETE,
                adminEntity(),
                ErrorResponse.class
        );

        assertNotFound(response, bookNotFoundMessage(MISSING_BOOK_ID));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingBookWithMissingAuthor() {
        BookRequest request = new BookRequest(
                "New Book",
                "8888888888888",
                2024,
                MISSING_AUTHOR_ID
        );

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url("/books"),
                POST,
                adminEntity(request),
                ErrorResponse.class
        );

        assertAuthorNotFound(response, authorNotFoundMessage(MISSING_AUTHOR_ID));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingBookWithMissingAuthor() {
        BookRequest request = new BookRequest(
                "Effective Java Updated",
                "9780134685991",
                2018,
                MISSING_AUTHOR_ID
        );

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url("/books/" + EXISTING_BOOK_ID),
                PUT,
                adminEntity(request),
                ErrorResponse.class
        );

        assertAuthorNotFound(response, authorNotFoundMessage(MISSING_AUTHOR_ID));
    }

    private void assertNotFound(ResponseEntity<ErrorResponse> response, String expectedMessage) {
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(expectedMessage);
        assertThat(response.getBody().status()).isEqualTo(NOT_FOUND.value());
    }

    private void assertAuthorNotFound(ResponseEntity<ErrorResponse> response, String expectedMessage) {
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo(expectedMessage);
        assertThat(response.getBody().status()).isEqualTo(NOT_FOUND.value());
    }

    private String bookNotFoundMessage(long bookId) {
        return "Book not found with id " + bookId;
    }

    private String authorNotFoundMessage(long authorId) {
        return "Author not found with id " + authorId;
    }
}