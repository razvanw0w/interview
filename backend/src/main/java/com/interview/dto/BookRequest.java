package com.interview.dto;

public record BookRequest(
        String title,
        String isbn,
        Integer publishedYear,
        Long authorId
) {
}
