package com.notes.backend.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Partial update of a user. Every field is optional; {@code null} means "leave
 * unchanged". Validation applies only when a value is present.
 */
public record UpdateUserRequest(
        @Email @Size(max = 255) String email,
        @Size(min = 3, max = 100) String username,
        Boolean enabled) {
}
