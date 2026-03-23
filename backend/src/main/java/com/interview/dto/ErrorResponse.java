package com.interview.dto;

import java.time.Instant;

public record ErrorResponse(
        String error,
        int status,
        String path,
        Instant timestamp
) {
}
