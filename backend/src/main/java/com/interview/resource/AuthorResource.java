package com.interview.resource;

import com.interview.dto.AuthorRequest;
import com.interview.dto.AuthorResponse;
import com.interview.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorResource {

    private final AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@RequestBody AuthorRequest request) {
        return authorService.create(request);
    }

    @GetMapping
    public List<AuthorResponse> getAll() {
        return authorService.getAll();
    }

    @GetMapping("/{id}")
    public AuthorResponse getById(@PathVariable Long id) {
        return authorService.getById(id);
    }

    @PutMapping("/{id}")
    public AuthorResponse update(@PathVariable Long id, @RequestBody AuthorRequest request) {
        return authorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.delete(id);
    }
}
