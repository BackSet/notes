package com.notes.backend.auth;

/**
 * Raised when login fails. The message is intentionally generic so it never
 * reveals whether the email/username exists (HTTP 401).
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
