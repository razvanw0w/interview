package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.config.WebMvcTestSecurityConfig;
import com.interview.dto.BookRequest;
import com.interview.dto.BookResponse;
import com.interview.exception.ResourceNotFoundException;
import com.interview.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookResource.class)
@Import({WebMvcTestSecurityConfig.class, GlobalExceptionHandler.class})
class BookResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor userJwt() {
        return jwt().authorities(() -> "ROLE_USER");
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
    }

    @Test
    void shouldReturnUnauthorizedWhenGettingAllBooksWithoutToken() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetAllBooksAsUser() throws Exception {
        List<BookResponse> books = List.of(
                new BookResponse(1L, "Effective Java", "9780134685991", 2018, 1L, "Joshua Bloch")
        );

        when(bookService.getAll()).thenReturn(books);

        mockMvc.perform(get("/books").with(userJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Effective Java"))
                .andExpect(jsonPath("$[0].authorName").value("Joshua Bloch"));
    }

    @Test
    void shouldGetBookByIdAsUser() throws Exception {
        BookResponse response = new BookResponse(
                1L,
                "Effective Java",
                "9780134685991",
                2018,
                1L,
                "Joshua Bloch"
        );

        when(bookService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/books/1").with(userJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    void shouldForbidCreateBookAsUser() throws Exception {
        BookRequest request = new BookRequest("Effective Java", "9780134685991", 2018, 1L);

        mockMvc.perform(post("/books")
                        .with(userJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(bookService, never()).create(any());
    }

    @Test
    void shouldCreateBookAsAdmin() throws Exception {
        BookRequest request = new BookRequest("Effective Java", "9780134685991", 2018, 1L);
        BookResponse response = new BookResponse(1L, "Effective Java", "9780134685991", 2018, 1L, "Joshua Bloch");

        when(bookService.create(eq(request))).thenReturn(response);

        mockMvc.perform(post("/books")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.authorId").value(1));
    }

    @Test
    void shouldForbidUpdateBookAsUser() throws Exception {
        BookRequest request = new BookRequest("Effective Java 3rd Edition", "9780134685991", 2018, 1L);

        mockMvc.perform(put("/books/1")
                        .with(userJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(bookService, never()).update(anyLong(), any());
    }

    @Test
    void shouldUpdateBookAsAdmin() throws Exception {
        BookRequest request = new BookRequest("Effective Java 3rd Edition", "9780134685991", 2018, 1L);
        BookResponse response = new BookResponse(1L, "Effective Java 3rd Edition", "9780134685991", 2018, 1L, "Joshua Bloch");

        when(bookService.update(1L, request)).thenReturn(response);

        mockMvc.perform(put("/books/1")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java 3rd Edition"));
    }

    @Test
    void shouldForbidDeleteBookAsUser() throws Exception {
        mockMvc.perform(delete("/books/1").with(userJwt()))
                .andExpect(status().isForbidden());

        verify(bookService, never()).delete(anyLong());
    }

    @Test
    void shouldDeleteBookAsAdmin() throws Exception {
        doNothing().when(bookService).delete(1L);

        mockMvc.perform(delete("/books/1").with(adminJwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExistAsUser() throws Exception {
        when(bookService.getById(999L))
                .thenThrow(new ResourceNotFoundException("Book not found with id 999"));

        mockMvc.perform(get("/books/999").with(userJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingBookAsAdmin() throws Exception {
        BookRequest request = new BookRequest(
                "Missing Book",
                "9999999999999",
                2024,
                1L
        );

        when(bookService.update(999L, request))
                .thenThrow(new ResourceNotFoundException("Book not found with id 999"));

        mockMvc.perform(put("/books/999")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingBookAsAdmin() throws Exception {
        doThrow(new ResourceNotFoundException("Book not found with id 999"))
                .when(bookService).delete(999L);

        mockMvc.perform(delete("/books/999").with(adminJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingBookWithMissingAuthorAsAdmin() throws Exception {
        BookRequest request = new BookRequest(
                "New Book",
                "8888888888888",
                2024,
                999L
        );

        when(bookService.create(request))
                .thenThrow(new ResourceNotFoundException("Author not found with id 999"));

        mockMvc.perform(post("/books")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingBookWithMissingAuthorAsAdmin() throws Exception {
        BookRequest request = new BookRequest(
                "Effective Java Updated",
                "9780134685991",
                2018,
                999L
        );

        when(bookService.update(1L, request))
                .thenThrow(new ResourceNotFoundException("Author not found with id 999"));

        mockMvc.perform(put("/books/1")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }
}