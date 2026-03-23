package com.interview.dto;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        Integer publishedYear,
        Long authorId,
        String authorName
) {
}
