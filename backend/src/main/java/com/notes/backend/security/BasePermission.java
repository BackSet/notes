package com.notes.backend.security;

/**
 * Catalog of base permissions seeded on startup and assigned in full to the
 * {@link RoleName#ADMIN} role. Extend this enum to introduce new permissions;
 * the bootstrap seeder upserts every value defined here.
 */
public enum BasePermission {

    USER_READ("Read user accounts"),
    USER_CREATE("Create user accounts"),
    USER_UPDATE("Update user accounts"),
    USER_DISABLE("Disable user accounts"),
    ROLE_READ("Read roles"),
    PERMISSION_READ("Read permissions");

    private final String description;

    BasePermission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
