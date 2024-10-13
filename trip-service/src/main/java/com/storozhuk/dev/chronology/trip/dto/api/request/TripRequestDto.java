package com.storozhuk.dev.chronology.trip.dto.api.request;

import com.storozhuk.dev.chronology.trip.validation.ValidTripDates;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@ValidTripDates
public record TripRequestDto(
    @NotBlank(message = "Name is required") String name,
    @NotNull(message = "Start date is required") LocalDateTime startDate,
    @NotNull(message = "End date is required") LocalDateTime endDate,
    Set<UUID> placeIds,
    Set<UUID> sharedUserIds) {}
