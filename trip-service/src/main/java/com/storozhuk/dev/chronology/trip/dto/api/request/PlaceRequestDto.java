package com.storozhuk.dev.chronology.trip.dto.api.request;

import jakarta.validation.constraints.NotBlank;

public record PlaceRequestDto(
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "Country is required") String country,
    String description) {}
