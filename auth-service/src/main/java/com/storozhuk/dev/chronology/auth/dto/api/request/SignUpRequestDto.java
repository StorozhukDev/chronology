package com.storozhuk.dev.chronology.auth.dto.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequestDto(
    @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
    @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])\\S+$",
            message = "Password must contain uppercase, lowercase letters, and digits")
        String password,
    @NotBlank(message = "Name is required") String name) {}
