package com.interview.dto;

import java.util.List;

public record RestPagedResponse<T>(
        List<T> content,
        PageMetadata page
) {
}
