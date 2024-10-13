package com.storozhuk.dev.chronology.auth.dto.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
    @NotBlank(message = "Password is required") String password) {}
