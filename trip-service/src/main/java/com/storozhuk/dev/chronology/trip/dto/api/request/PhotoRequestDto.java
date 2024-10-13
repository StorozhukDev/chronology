package com.storozhuk.dev.chronology.trip.dto.api.request;

import jakarta.validation.constraints.NotBlank;

public record PhotoRequestDto(@NotBlank(message = "URL is required") String url) {}
