package com.notes.backend.auth;

/** Raised on registration when the email is already taken (HTTP 409). */
public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException() {
        super("Email is already in use");
    }
}
