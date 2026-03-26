package com.interview.resource;

import com.interview.dto.BookRequest;
import com.interview.dto.BookResponse;
import com.interview.openapi.*;
import com.interview.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Operations for managing books")
@SecurityRequirement(name = "bearerAuth")
@StandardSecurityResponses
public class BookResource {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create book",
            description = "Creates a new book. Requires ADMIN role."
    )
    @CreatedApiResponse
    @BadRequestApiResponse
    @NotFoundApiResponse
    public BookResponse create(@RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get all books",
            description = "Returns paginated books. Supports pagination and sorting. Requires USER or ADMIN role."
    )
    @OkApiResponse
    public Page<BookResponse> getAll(
            @ParameterObject
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return bookService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get book by id",
            description = "Returns a single book by id. Requires USER or ADMIN role."
    )
    @OkApiResponse
    @NotFoundApiResponse
    public BookResponse getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update book",
            description = "Updates an existing book by id. Requires ADMIN role."
    )
    @OkApiResponse
    @BadRequestApiResponse
    @NotFoundApiResponse
    public BookResponse update(@PathVariable Long id, @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete book",
            description = "Deletes a book by id. Requires ADMIN role."
    )
    @NoContentApiResponse
    @NotFoundApiResponse
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}