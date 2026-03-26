package com.interview.service;

import com.interview.dto.AuthorResponse;
import com.interview.dto.BookRequest;
import com.interview.dto.BookResponse;
import com.interview.entity.Author;
import com.interview.entity.Book;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.AuthorRepository;
import com.interview.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookResponse create(BookRequest request) {
        log.info("Creating book with isbn={} for authorId={}", request.isbn(), request.authorId());

        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    log.error("Author not found for book creation, authorId={}", request.authorId());
                    return new ResourceNotFoundException("Author not found with id %s".formatted(request.authorId()));
                });

        Book book = Book.builder()
                .title(request.title())
                .isbn(request.isbn())
                .publishedYear(request.publishedYear())
                .author(author)
                .build();

        BookResponse response = toResponse(bookRepository.save(book));
        log.info("Created book with id={} and isbn={}", response.id(), response.isbn());

        return response;
    }

    public Page<BookResponse> getAll(Pageable pageable) {
        log.info("Fetching books page={} size={} sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<BookResponse> responses = bookRepository.findAll(pageable)
                .map(BookService::toResponse);

        log.info("Fetched {} books on current page, totalElements={}",
                responses.getNumberOfElements(),
                responses.getTotalElements());

        return responses;
    }

    @Cacheable(value = "bookById", key = "#id")
    public BookResponse getById(Long id) {
        log.info("Fetching book by id={}", id);

        BookResponse response = toResponse(findBook(id));
        log.info("Fetched book id={}", id);

        return response;
    }

    @CacheEvict(value = "bookById", key = "#id")
    public BookResponse update(Long id, BookRequest request) {
        log.info("Updating book id={} with isbn={} and authorId={}", id, request.isbn(), request.authorId());

        Book book = findBook(id);

        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    log.error("Author not found for book update, bookId={}, authorId={}", id, request.authorId());
                    return new ResourceNotFoundException("Author not found with id %d".formatted(request.authorId()));
                });

        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        book.setAuthor(author);

        BookResponse response = toResponse(bookRepository.save(book));
        log.info("Updated book id={}", id);

        return response;
    }

    @CacheEvict(value = "bookById", key = "#id")
    public void delete(Long id) {
        log.info("Deleting book id={}", id);

        Book book = findBook(id);
        bookRepository.delete(book);

        log.info("Deleted book id={}", id);
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found with id={}", id);
                    return new ResourceNotFoundException("Book not found with id %d".formatted(id));
                });
    }

    private static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        );
    }
}