package com.notes.backend.security;

/**
 * Raised when a refresh token is unknown, revoked or expired. Maps to HTTP 401
 * with a generic message so it cannot be used to probe token validity.
 */
public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super("Invalid refresh token");
    }
}
