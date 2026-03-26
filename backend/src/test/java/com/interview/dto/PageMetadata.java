package com.interview.dto;

public record PageMetadata(
        int size,
        long totalElements,
        int totalPages,
        int number
) {
}
