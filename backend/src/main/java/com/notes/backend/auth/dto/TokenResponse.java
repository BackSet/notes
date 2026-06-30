package com.notes.backend.auth.dto;

/** Access + refresh token pair. {@code expiresInMs} is the access token lifetime. */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInMs) {

    public static TokenResponse bearer(String accessToken, String refreshToken, long expiresInMs) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresInMs);
    }
}
