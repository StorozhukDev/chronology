package com.storozhuk.dev.chronology.trip.dto.api.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TripResponseDto(
    UUID id,
    String name,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Set<UUID> placeIds,
    Set<UUID> sharedUserIds) {}
