package com.interview.dto;

public record BookSummaryResponse(
        Long id,
        String title,
        String isbn,
        Integer publishedYear
) {
}
