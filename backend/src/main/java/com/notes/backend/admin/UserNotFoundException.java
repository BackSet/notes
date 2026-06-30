package com.notes.backend.admin;

/** Raised when an admin operation targets a non-existent user (HTTP 404). */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}
