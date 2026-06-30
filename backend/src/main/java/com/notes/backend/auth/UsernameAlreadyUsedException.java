package com.notes.backend.auth;

/** Raised on registration when the username is already taken (HTTP 409). */
public class UsernameAlreadyUsedException extends RuntimeException {

    public UsernameAlreadyUsedException() {
        super("Username is already in use");
    }
}
