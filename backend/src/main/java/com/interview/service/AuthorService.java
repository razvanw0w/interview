package com.interview.service;

import com.interview.dto.AuthorRequest;
import com.interview.dto.AuthorResponse;
import com.interview.dto.BookSummaryResponse;
import com.interview.entity.Author;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.AuthorRepository;
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
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorResponse create(AuthorRequest request) {
        log.info("Creating author with email={}", request.email());

        Author author = Author.builder()
                .name(request.name())
                .email(request.email())
                .build();

        AuthorResponse response = toResponse(authorRepository.save(author));
        log.info("Created author with id={}", response.id());

        return response;
    }

    public Page<AuthorResponse> getAll(Pageable pageable) {
        log.info("Fetching authors page={} size={} sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<AuthorResponse> responses = authorRepository.findAll(pageable)
                .map(AuthorService::toResponse);

        log.info("Fetched {} authors on current page, totalElements={}",
                responses.getNumberOfElements(),
                responses.getTotalElements());

        return responses;
    }

    @Cacheable(value = "authorById", key = "#id")
    public AuthorResponse getById(Long id) {
        log.info("Fetching author by id={}", id);
        AuthorResponse response = toResponse(findAuthor(id));
        log.info("Fetched author id={}", id);
        return response;
    }

    @CacheEvict(value = "authorById", key = "#id")
    public AuthorResponse update(Long id, AuthorRequest request) {
        log.info("Updating author id={} with email={}", id, request.email());

        Author author = findAuthor(id);
        author.setName(request.name());
        author.setEmail(request.email());

        AuthorResponse response = toResponse(authorRepository.save(author));
        log.info("Updated author id={}", id);

        return response;
    }

    @CacheEvict(value = "authorById", key = "#id")
    public void delete(Long id) {
        log.info("Deleting author id={}", id);

        Author author = findAuthor(id);
        authorRepository.delete(author);

        log.info("Deleted author id={}", id);
    }

    private Author findAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id={}", id);
                    return new ResourceNotFoundException("Author not found with id %d".formatted(id));
                });
    }

    private static AuthorResponse toResponse(Author author) {
        List<BookSummaryResponse> books = author.getBooks() == null
                ? List.of()
                : author.getBooks()
                .stream()
                .map(book -> new BookSummaryResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getPublishedYear()
                ))
                .toList();

        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getEmail(),
                books
        );
    }
}