package com.notes.backend.auth.dto;

import java.util.List;

/** Current user view. Never exposes the password hash. */
public record MeResponse(
        Long id,
        String username,
        String email,
        boolean enabled,
        List<String> roles,
        List<String> permissions) {
}
