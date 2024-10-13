package com.storozhuk.dev.chronology.trip.service;

import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;
import com.storozhuk.dev.chronology.trip.dto.api.request.PhotoRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.request.PlaceRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PhotoResponseDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PlaceResponseDto;
import com.storozhuk.dev.chronology.trip.entity.PlaceEntity;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for managing places within the trip service. */
public interface PlaceService {

  /**
   * Creates a new place based on the provided place request data.
   *
   * @param placeRequestDto the data transfer object containing place details
   * @return the response DTO containing the details of the created place
   */
  PlaceResponseDto createPlace(PlaceRequestDto placeRequestDto);

  /**
   * Retrieves a place by its unique identifier.
   *
   * @param placeId the UUID of the place to retrieve
   * @return the response DTO containing the place details
   * @throws NotFoundException if the place with the specified ID is not found
   */
  PlaceResponseDto getPlace(UUID placeId);

  /**
   * Retrieves a paginated list of places based on filters such as country, user ID, start date, and
   * end date.
   *
   * @param country the country to filter places by (optional)
   * @param userId the UUID of the user associated with trips containing the places (optional)
   * @param startDate the start date to filter trips by (optional)
   * @param endDate the end date to filter trips by (optional)
   * @param pageable pagination information
   * @return a page of place response DTOs matching the criteria
   */
  Page<PlaceResponseDto> getPlaces(
      String country,
      UUID userId,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Pageable pageable);

  /**
   * Retrieves a set of place entities by their unique identifiers.
   *
   * @param placeIds the set of UUIDs of the places to retrieve
   * @return a set of place entities matching the provided IDs
   * @throws NotFoundException if any of the places are not found
   */
  Set<PlaceEntity> getPlacesByIds(Set<UUID> placeIds);

  /**
   * Updates an existing place with new data.
   *
   * @param placeId the UUID of the place to update
   * @param placeRequestDto the data transfer object containing updated place details
   * @return the response DTO containing the updated place details
   * @throws NotFoundException if the place with the specified ID is not found
   */
  PlaceResponseDto updatePlace(UUID placeId, PlaceRequestDto placeRequestDto);

  /**
   * Deletes a place by its unique identifier.
   *
   * @param placeId the UUID of the place to delete
   * @throws NotFoundException if the place with the specified ID is not found
   */
  void deletePlace(UUID placeId);

  /**
   * Adds a photo to a place.
   *
   * @param placeId the UUID of the place to add the photo to
   * @param photoRequestDto the data transfer object containing photo details
   * @return the response DTO containing the details of the added photo
   * @throws NotFoundException if the place with the specified ID is not found
   */
  PhotoResponseDto addPhotoToPlace(UUID placeId, PhotoRequestDto photoRequestDto);
}
