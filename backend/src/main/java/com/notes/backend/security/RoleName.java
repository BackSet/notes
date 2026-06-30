package com.notes.backend.security;

/**
 * Canonical role names. Only {@code ADMIN} exists in this iteration; publicly
 * registered users will receive a separate, non-admin role in a later one.
 */
public final class RoleName {

    public static final String ADMIN = "ADMIN";

    private RoleName() {
    }
}
