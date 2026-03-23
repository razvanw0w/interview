package com.interview.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
