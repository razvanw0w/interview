package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.config.WebMvcTestSecurityConfig;
import com.interview.dto.AuthorRequest;
import com.interview.dto.AuthorResponse;
import com.interview.exception.ResourceNotFoundException;
import com.interview.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorResource.class)
@Import({WebMvcTestSecurityConfig.class, GlobalExceptionHandler.class})
class AuthorResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor userJwt() {
        return jwt().authorities(() -> "ROLE_USER");
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
    }

    @Test
    void shouldReturnUnauthorizedWhenGettingAllAuthorsWithoutToken() throws Exception {
        mockMvc.perform(get("/authors"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetAllAuthorsAsUser() throws Exception {
        List<AuthorResponse> authors = List.of(
                new AuthorResponse(1L, "Joshua Bloch", "joshua.bloch@example.com", List.of()),
                new AuthorResponse(2L, "Robert C. Martin", "robert.martin@example.com", List.of())
        );

        given(authorService.getAll()).willReturn(authors);

        mockMvc.perform(get("/authors").with(userJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Joshua Bloch"))
                .andExpect(jsonPath("$[1].name").value("Robert C. Martin"));
    }

    @Test
    void shouldGetAuthorByIdAsUser() throws Exception {
        AuthorResponse author = new AuthorResponse(
                1L,
                "Joshua Bloch",
                "joshua.bloch@example.com",
                List.of()
        );

        given(authorService.getById(1L)).willReturn(author);

        mockMvc.perform(get("/authors/1").with(userJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Joshua Bloch"));
    }

    @Test
    void shouldForbidCreateAuthorAsUser() throws Exception {
        AuthorRequest request = new AuthorRequest("Joshua Bloch", "joshua.bloch@example.com");

        mockMvc.perform(post("/authors")
                        .with(userJwt())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(authorService, never()).create(any());
    }

    @Test
    void shouldCreateAuthorAsAdmin() throws Exception {
        AuthorRequest request = new AuthorRequest("Joshua Bloch", "joshua.bloch@example.com");
        AuthorResponse response = new AuthorResponse(1L, "Joshua Bloch", "joshua.bloch@example.com", List.of());

        given(authorService.create(eq(request))).willReturn(response);

        mockMvc.perform(post("/authors")
                        .with(adminJwt())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("joshua.bloch@example.com"));
    }

    @Test
    void shouldForbidUpdateAuthorAsUser() throws Exception {
        AuthorRequest request = new AuthorRequest("Updated Name", "updated@example.com");

        mockMvc.perform(put("/authors/1")
                        .with(userJwt())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(authorService, never()).update(anyLong(), any());
    }

    @Test
    void shouldUpdateAuthorAsAdmin() throws Exception {
        AuthorRequest request = new AuthorRequest("Updated Name", "updated@example.com");
        AuthorResponse response = new AuthorResponse(1L, "Updated Name", "updated@example.com", List.of());

        given(authorService.update(1L, request)).willReturn(response);

        mockMvc.perform(put("/authors/1")
                        .with(adminJwt())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldForbidDeleteAuthorAsUser() throws Exception {
        mockMvc.perform(delete("/authors/1").with(userJwt()))
                .andExpect(status().isForbidden());

        verify(authorService, never()).delete(anyLong());
    }

    @Test
    void shouldDeleteAuthorAsAdmin() throws Exception {
        doNothing().when(authorService).delete(1L);

        mockMvc.perform(delete("/authors/1").with(adminJwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenAuthorDoesNotExistAsUser() throws Exception {
        given(authorService.getById(999L))
                .willThrow(new ResourceNotFoundException("Author not found with id 999"));

        mockMvc.perform(get("/authors/999").with(userJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingAuthorAsAdmin() throws Exception {
        AuthorRequest request = new AuthorRequest("Missing Author", "missing@example.com");

        given(authorService.update(999L, request))
                .willThrow(new ResourceNotFoundException("Author not found with id 999"));

        mockMvc.perform(put("/authors/999")
                        .with(adminJwt())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingAuthorAsAdmin() throws Exception {
        doThrow(new ResourceNotFoundException("Author not found with id 999"))
                .when(authorService).delete(999L);

        mockMvc.perform(delete("/authors/999").with(adminJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Author not found with id 999"));
    }
}