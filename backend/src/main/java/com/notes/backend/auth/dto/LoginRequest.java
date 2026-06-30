package com.notes.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** {@code identifier} accepts either the user's email or username. */
public record LoginRequest(
        @NotBlank String identifier,
        @NotBlank String password) {
}
