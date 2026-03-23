package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookResource.class)
@Import(GlobalExceptionHandler.class)
class BookResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void shouldGetAllBooks() throws Exception {
        List<BookResponse> books = List.of(
                new BookResponse(1L, "Effective Java", "9780134685991", 2018, 1L, "Joshua Bloch")
        );

        when(bookService.getAll()).thenReturn(books);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Effective Java"))
                .andExpect(jsonPath("$[0].authorName").value("Joshua Bloch"));
    }

    @Test
    void shouldGetBookById() throws Exception {
        BookResponse response = new BookResponse(
                1L,
                "Effective Java",
                "9780134685991",
                2018,
                1L,
                "Joshua Bloch"
        );

        when(bookService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    void shouldCreateBook() throws Exception {
        BookRequest request = new BookRequest("Effective Java", "9780134685991", 2018, 1L);
        BookResponse response = new BookResponse(1L, "Effective Java", "9780134685991", 2018, 1L, "Joshua Bloch");

        when(bookService.create(eq(request))).thenReturn(response);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.authorId").value(1));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        BookRequest request = new BookRequest("Effective Java 3rd Edition", "9780134685991", 2018, 1L);
        BookResponse response = new BookResponse(1L, "Effective Java 3rd Edition", "9780134685991", 2018, 1L, "Joshua Bloch");

        when(bookService.update(1L, request)).thenReturn(response);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java 3rd Edition"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).delete(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        when(bookService.getById(999L))
                .thenThrow(new ResourceNotFoundException("Book not found with id 999"));

        mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingBook() throws Exception {
        BookRequest request = new BookRequest(
                "Missing Book",
                "9999999999999",
                2024,
                1L
        );

        when(bookService.update(999L, request))
                .thenThrow(new ResourceNotFoundException("Book not found with id 999"));

        mockMvc.perform(put("/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingBook() throws Exception {
        doThrow(new ResourceNotFoundException("Book not found with id 999"))
                .when(bookService).delete(999L);

        mockMvc.perform(delete("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingBookWithMissingAuthor() throws Exception {
        BookRequest request = new BookRequest(
                "New Book",
                "8888888888888",
                2024,
                999L
        );

        when(bookService.create(request))
                .thenThrow(new ResourceNotFoundException("Author not found with id 999"));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingBookWithMissingAuthor() throws Exception {
        BookRequest request = new BookRequest(
                "Effective Java Updated",
                "9780134685991",
                2018,
                999L
        );

        when(bookService.update(1L, request))
                .thenThrow(new ResourceNotFoundException("Author not found with id 999"));

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }
}
