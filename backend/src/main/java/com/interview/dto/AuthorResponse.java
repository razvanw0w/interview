package com.interview.dto;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        String email,
        List<BookSummaryResponse> books
) {
}
