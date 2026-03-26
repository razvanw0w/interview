package com.interview.resource;

import com.interview.dto.AuthorRequest;
import com.interview.dto.AuthorResponse;
import com.interview.openapi.*;
import com.interview.service.AuthorService;
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
@RequestMapping("/authors")
@RequiredArgsConstructor
@Tag(name = "Authors", description = "Operations for managing authors")
@SecurityRequirement(name = "bearerAuth")
@StandardSecurityResponses
public class AuthorResource {

    private final AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create author",
            description = "Creates a new author. Requires ADMIN role."
    )
    @CreatedApiResponse
    @BadRequestApiResponse
    public AuthorResponse create(@RequestBody AuthorRequest request) {
        return authorService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get all authors",
            description = "Returns paginated authors. Supports pagination and sorting. Requires USER or ADMIN role."
    )
    @OkApiResponse
    public Page<AuthorResponse> getAll(
            @ParameterObject
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return authorService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get author by id",
            description = "Returns a single author by id. Requires USER or ADMIN role."
    )
    @OkApiResponse
    @NotFoundApiResponse
    public AuthorResponse getById(@PathVariable Long id) {
        return authorService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update author",
            description = "Updates an existing author by id. Requires ADMIN role."
    )
    @OkApiResponse
    @BadRequestApiResponse
    @NotFoundApiResponse
    public AuthorResponse update(@PathVariable Long id, @RequestBody AuthorRequest request) {
        return authorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete author",
            description = "Deletes an author by id. Requires ADMIN role."
    )
    @NoContentApiResponse
    @NotFoundApiResponse
    public void delete(@PathVariable Long id) {
        authorService.delete(id);
    }
}