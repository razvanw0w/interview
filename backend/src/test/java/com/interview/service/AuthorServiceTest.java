package com.interview.service;

import com.interview.dto.AuthorRequest;
import com.interview.dto.AuthorResponse;
import com.interview.entity.Author;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void shouldCreateAuthor() {
        AuthorRequest request = new AuthorRequest("Joshua Bloch", "joshua.bloch@example.com");

        Author savedAuthor = Author.builder()
                .id(1L)
                .name(request.name())
                .email(request.email())
                .books(new ArrayList<>())
                .build();

        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

        AuthorResponse response = authorService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Joshua Bloch", response.name());
        assertEquals("joshua.bloch@example.com", response.email());
        assertTrue(response.books().isEmpty());

        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void shouldReturnAllAuthors() {
        Author author1 = Author.builder()
                .id(1L)
                .name("Joshua Bloch")
                .email("joshua.bloch@example.com")
                .books(new ArrayList<>())
                .build();

        Author author2 = Author.builder()
                .id(2L)
                .name("Robert C. Martin")
                .email("robert.martin@example.com")
                .books(new ArrayList<>())
                .build();

        when(authorRepository.findAll()).thenReturn(List.of(author1, author2));

        List<AuthorResponse> responses = authorService.getAll();

        assertEquals(2, responses.size());
        assertEquals("Joshua Bloch", responses.get(0).name());
        assertEquals("Robert C. Martin", responses.get(1).name());
        verify(authorRepository).findAll();
    }

    @Test
    void shouldReturnAuthorById() {
        Author author = Author.builder()
                .id(1L)
                .name("Joshua Bloch")
                .email("joshua.bloch@example.com")
                .books(new ArrayList<>())
                .build();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorResponse response = authorService.getById(1L);

        assertEquals(1L, response.id());
        assertEquals("Joshua Bloch", response.name());
        verify(authorRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenAuthorNotFoundById() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.getById(99L));

        verify(authorRepository).findById(99L);
    }

    @Test
    void shouldUpdateAuthor() {
        Author existing = Author.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .books(new ArrayList<>())
                .build();

        Author updated = Author.builder()
                .id(1L)
                .name("New Name")
                .email("new@example.com")
                .books(new ArrayList<>())
                .build();

        AuthorRequest request = new AuthorRequest("New Name", "new@example.com");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(authorRepository.save(existing)).thenReturn(updated);

        AuthorResponse response = authorService.update(1L, request);

        assertEquals("New Name", response.name());
        assertEquals("new@example.com", response.email());
        verify(authorRepository).findById(1L);
        verify(authorRepository).save(existing);
    }

    @Test
    void shouldDeleteAuthor() {
        Author author = Author.builder()
                .id(1L)
                .name("Joshua Bloch")
                .email("joshua.bloch@example.com")
                .books(new ArrayList<>())
                .build();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        authorService.delete(1L);

        verify(authorRepository).findById(1L);
        verify(authorRepository).delete(author);
    }

    @Test
    void shouldThrowWhenUpdatingMissingAuthor() {
        AuthorRequest request = new AuthorRequest("Missing Author", "missing@example.com");

        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.update(99L, request)
        );

        assertEquals("Author not found with id 99", ex.getMessage());
        verify(authorRepository).findById(99L);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void shouldThrowWhenDeletingMissingAuthor() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.delete(99L)
        );

        assertEquals("Author not found with id 99", ex.getMessage());
        verify(authorRepository).findById(99L);
        verify(authorRepository, never()).delete(any(Author.class));
    }
}