package com.storozhuk.dev.chronology.trip.service.facade;

import com.storozhuk.dev.chronology.exception.handler.exception.AccessDeniedException;
import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;
import com.storozhuk.dev.chronology.exception.handler.exception.ValidationException;
import com.storozhuk.dev.chronology.trip.dto.api.request.TripRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.TripResponseDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Facade interface for handling trip-related operations with business logic. */
public interface TripFacade {

  /**
   * Creates a new trip.
   *
   * @param userId the UUID of the user creating the trip
   * @param tripRequestDto the trip request DTO containing trip details
   * @return the trip response DTO with created trip information
   * @throws ValidationException if validation fails
   * @throws NotFoundException if referenced places are not found
   */
  TripResponseDto createTrip(UUID userId, TripRequestDto tripRequestDto);

  /**
   * Retrieves a trip by its ID.
   *
   * @param userId the UUID of the requesting user
   * @param tripId the UUID of the trip
   * @return the trip response DTO
   * @throws AccessDeniedException if the user doesn't have access
   * @throws NotFoundException if the trip is not found
   */
  TripResponseDto getTrip(UUID userId, UUID tripId);

  /**
   * Retrieves a page of trips shared with the user.
   *
   * @param userId ID of the user
   * @param pageable Pagination information
   * @return Page of TripResponseDto
   */
  Page<TripResponseDto> getTrips(UUID userId, Pageable pageable);

  /**
   * Updates an existing trip.
   *
   * @param userId the UUID of the user updating the trip
   * @param tripId the UUID of the trip
   * @param tripRequestDto the trip request DTO with updated details
   * @return the updated trip response DTO
   * @throws AccessDeniedException if the user is not the owner
   * @throws NotFoundException if the trip or referenced places are not found
   */
  TripResponseDto updateTrip(UUID userId, UUID tripId, TripRequestDto tripRequestDto);

  /**
   * Deletes a trip by its ID.
   *
   * @param userId the UUID of the user deleting the trip
   * @param tripId the UUID of the trip
   * @throws AccessDeniedException if the user is not the owner
   * @throws NotFoundException if the trip is not found
   */
  void deleteTrip(UUID userId, UUID tripId);
}
