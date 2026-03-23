package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.AuthorRequest;
import com.interview.dto.AuthorResponse;
import com.interview.exception.ResourceNotFoundException;
import com.interview.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(AuthorResource.class)
@Import(GlobalExceptionHandler.class)
class AuthorResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService authorService;

    @Test
    void shouldGetAllAuthors() throws Exception {
        List<AuthorResponse> authors = List.of(
                new AuthorResponse(1L, "Joshua Bloch", "joshua.bloch@example.com", List.of()),
                new AuthorResponse(2L, "Robert C. Martin", "robert.martin@example.com", List.of())
        );

        when(authorService.getAll()).thenReturn(authors);

        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Joshua Bloch"))
                .andExpect(jsonPath("$[1].name").value("Robert C. Martin"));
    }

    @Test
    void shouldGetAuthorById() throws Exception {
        AuthorResponse author = new AuthorResponse(
                1L,
                "Joshua Bloch",
                "joshua.bloch@example.com",
                List.of()
        );

        when(authorService.getById(1L)).thenReturn(author);

        mockMvc.perform(get("/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Joshua Bloch"));
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        AuthorRequest request = new AuthorRequest("Joshua Bloch", "joshua.bloch@example.com");
        AuthorResponse response = new AuthorResponse(1L, "Joshua Bloch", "joshua.bloch@example.com", List.of());

        when(authorService.create(eq(request))).thenReturn(response);

        mockMvc.perform(post("/authors")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("joshua.bloch@example.com"));
    }

    @Test
    void shouldUpdateAuthor() throws Exception {
        AuthorRequest request = new AuthorRequest("Updated Name", "updated@example.com");
        AuthorResponse response = new AuthorResponse(1L, "Updated Name", "updated@example.com", List.of());

        when(authorService.update(1L, request)).thenReturn(response);

        mockMvc.perform(put("/authors/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        doNothing().when(authorService).delete(1L);

        mockMvc.perform(delete("/authors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenAuthorDoesNotExist() throws Exception {
        when(authorService.getById(999L))
                .thenThrow(new ResourceNotFoundException("Author not found with id 999"));

        mockMvc.perform(get("/authors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingAuthor() throws Exception {
        AuthorRequest request = new AuthorRequest(
                "Missing Author",
                "missing@example.com"
        );

        when(authorService.update(999L, request))
                .thenThrow(new ResourceNotFoundException("Author not found with id 999"));

        mockMvc.perform(put("/authors/999")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingAuthor() throws Exception {
        doThrow(new ResourceNotFoundException("Author not found with id 999"))
                .when(authorService).delete(999L);

        mockMvc.perform(delete("/authors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }
}
