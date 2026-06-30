package com.notes.backend.admin;

/**
 * Raised when an operation would leave the system without any enabled administrator
 * (HTTP 409). Protects the last enabled admin from being disabled.
 */
public class LastAdminException extends RuntimeException {

    public LastAdminException() {
        super("Cannot disable the last enabled administrator");
    }
}
