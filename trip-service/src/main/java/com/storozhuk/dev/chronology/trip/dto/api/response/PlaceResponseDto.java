package com.storozhuk.dev.chronology.trip.dto.api.response;

import java.util.List;
import java.util.UUID;

public record PlaceResponseDto(
    UUID id, String name, String country, String description, List<PhotoResponseDto> photos) {}
