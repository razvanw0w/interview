package com.interview.service;

import com.interview.dto.BookRequest;
import com.interview.dto.BookResponse;
import com.interview.entity.Author;
import com.interview.entity.Book;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.AuthorRepository;
import com.interview.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldCreateBook() {
        Author author = Author.builder()
                .id(1L)
                .name("Joshua Bloch")
                .email("joshua.bloch@example.com")
                .books(new ArrayList<>())
                .build();

        BookRequest request = new BookRequest(
                "Effective Java",
                "9780134685991",
                2018,
                1L
        );

        Book savedBook = Book.builder()
                .id(10L)
                .title(request.title())
                .isbn(request.isbn())
                .publishedYear(request.publishedYear())
                .author(author)
                .build();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        BookResponse response = bookService.create(request);

        assertEquals(10L, response.id());
        assertEquals("Effective Java", response.title());
        assertEquals(1L, response.authorId());
        assertEquals("Joshua Bloch", response.authorName());

        verify(authorRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldThrowWhenCreatingBookWithMissingAuthor() {
        BookRequest request = new BookRequest(
                "Effective Java",
                "9780134685991",
                2018,
                999L
        );

        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.create(request));

        verify(authorRepository).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldMapBooksFromPageToResponses() {
        Author author = Author.builder()
                .id(1L)
                .name("Joshua Bloch")
                .email("joshua.bloch@example.com")
                .books(new ArrayList<>())
                .build();

        Book book = Book.builder()
                .id(1L)
                .title("Effective Java")
                .isbn("9780134685991")
                .publishedYear(2018)
                .author(author)
                .build();

        Pageable pageable = PageRequest.of(0, 1);
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<BookResponse> responses = bookService.getAll(pageable);

        assertEquals("Effective Java", responses.getContent().get(0).title());
        assertEquals("Joshua Bloch", responses.getContent().get(0).authorName());

        verify(bookRepository).findAll(pageable);
    }

    @Test
    void shouldReturnBooksPaginationMetadata() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Book> bookPage = new PageImpl<>(List.of(), pageable, 5);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<BookResponse> responses = bookService.getAll(pageable);

        assertEquals(5, responses.getTotalElements());
        assertEquals(3, responses.getTotalPages());
        assertEquals(0, responses.getNumber());
        assertEquals(2, responses.getSize());

        verify(bookRepository).findAll(pageable);
    }

    @Test
    void shouldReturnBookById() {
        Author author = Author.builder()
                .id(1L)
                .name("Joshua Bloch")
                .email("joshua.bloch@example.com")
                .books(new ArrayList<>())
                .build();

        Book book = Book.builder()
                .id(1L)
                .title("Effective Java")
                .isbn("9780134685991")
                .publishedYear(2018)
                .author(author)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse response = bookService.getById(1L);

        assertEquals(1L, response.id());
        assertEquals("Effective Java", response.title());
        verify(bookRepository).findById(1L);
    }

    @Test
    void shouldUpdateBook() {
        Author oldAuthor = Author.builder()
                .id(1L)
                .name("Old Author")
                .email("old@example.com")
                .books(new ArrayList<>())
                .build();

        Author newAuthor = Author.builder()
                .id(2L)
                .name("Joshua Bloch")
                .email("joshua.bloch@example.com")
                .books(new ArrayList<>())
                .build();

        Book existingBook = Book.builder()
                .id(1L)
                .title("Old Title")
                .isbn("111")
                .publishedYear(2000)
                .author(oldAuthor)
                .build();

        BookRequest request = new BookRequest(
                "Effective Java",
                "9780134685991",
                2018,
                2L
        );

        Book updatedBook = Book.builder()
                .id(1L)
                .title("Effective Java")
                .isbn("9780134685991")
                .publishedYear(2018)
                .author(newAuthor)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);

        BookResponse response = bookService.update(1L, request);

        assertEquals("Effective Java", response.title());
        assertEquals(2L, response.authorId());
        assertEquals("Joshua Bloch", response.authorName());

        verify(bookRepository).findById(1L);
        verify(authorRepository).findById(2L);
        verify(bookRepository).save(existingBook);
    }

    @Test
    void shouldDeleteBook() {
        Author author = Author.builder()
                .id(1L)
                .name("Joshua Bloch")
                .email("joshua.bloch@example.com")
                .books(new ArrayList<>())
                .build();

        Book book = Book.builder()
                .id(1L)
                .title("Effective Java")
                .isbn("9780134685991")
                .publishedYear(2018)
                .author(author)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(book);
    }

    @Test
    void shouldThrowWhenBookNotFoundById() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.getById(99L)
        );

        assertEquals("Book not found with id 99", ex.getMessage());
        verify(bookRepository).findById(99L);
    }

    @Test
    void shouldThrowWhenUpdatingMissingBook() {
        BookRequest request = new BookRequest(
                "Missing Book",
                "9999999999999",
                2024,
                1L
        );

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.update(99L, request)
        );

        assertEquals("Book not found with id 99", ex.getMessage());
        verify(bookRepository).findById(99L);
        verify(authorRepository, never()).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldThrowWhenUpdatingBookWithMissingAuthor() {
        Author existingAuthor = Author.builder()
                .id(1L)
                .name("Existing Author")
                .email("existing@example.com")
                .books(new ArrayList<>())
                .build();

        Book existingBook = Book.builder()
                .id(1L)
                .title("Existing Book")
                .isbn("1234567890123")
                .publishedYear(2020)
                .author(existingAuthor)
                .build();

        BookRequest request = new BookRequest(
                "Updated Book",
                "1234567890123",
                2024,
                999L
        );

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.update(1L, request)
        );

        assertEquals("Author not found with id 999", ex.getMessage());
        verify(bookRepository).findById(1L);
        verify(authorRepository).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldThrowWhenDeletingMissingBook() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.delete(99L)
        );

        assertEquals("Book not found with id 99", ex.getMessage());
        verify(bookRepository).findById(99L);
        verify(bookRepository, never()).delete(any(Book.class));
    }
}
