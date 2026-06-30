package com.notes.backend.admin.dto;

import java.time.Instant;
import java.util.List;

/** Admin view of a user. Never exposes the password hash. */
public record UserSummaryResponse(
        Long id,
        String username,
        String email,
        boolean enabled,
        List<String> roles,
        Instant createdAt,
        Instant updatedAt) {
}
