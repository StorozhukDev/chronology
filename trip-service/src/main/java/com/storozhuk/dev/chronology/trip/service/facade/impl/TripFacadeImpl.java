package com.storozhuk.dev.chronology.trip.service.facade.impl;

import static com.storozhuk.dev.chronology.trip.util.TripConstants.ROLE_OWNER;
import static com.storozhuk.dev.chronology.trip.util.TripConstants.ROLE_VIEWER;
import static java.util.Objects.nonNull;

import com.storozhuk.dev.chronology.exception.handler.exception.AccessDeniedException;
import com.storozhuk.dev.chronology.trip.dto.api.request.TripRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.TripResponseDto;
import com.storozhuk.dev.chronology.trip.entity.PlaceEntity;
import com.storozhuk.dev.chronology.trip.entity.TripEntity;
import com.storozhuk.dev.chronology.trip.entity.TripSharedUserEntity;
import com.storozhuk.dev.chronology.trip.mapper.TripMapper;
import com.storozhuk.dev.chronology.trip.repository.TripSharedUserRepository;
import com.storozhuk.dev.chronology.trip.service.PlaceService;
import com.storozhuk.dev.chronology.trip.service.TripService;
import com.storozhuk.dev.chronology.trip.service.facade.TripFacade;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TripFacadeImpl is responsible for orchestrating trip-related operations that involve multiple
 * services like TripService and PlaceService. It ensures that business logic remains cohesive and
 * adheres to the Single Responsibility Principle.
 */
@Service
@RequiredArgsConstructor
public class TripFacadeImpl implements TripFacade {

  private final TripService tripService;
  private final PlaceService placeService;
  private final TripSharedUserRepository tripSharedUserRepository;
  private final TripMapper tripMapper;

  @Override
  @Transactional
  public TripResponseDto createTrip(UUID userId, TripRequestDto tripRequestDto) {
    // Fetch places
    Set<PlaceEntity> places = placeService.getPlacesByIds(tripRequestDto.placeIds());

    // Map DTO to entity
    TripEntity tripEntity = tripMapper.toTripEntity(tripRequestDto);
    tripEntity.setCreatedBy(userId);
    tripEntity.setPlaces(places);

    // Handle shared users
    Set<TripSharedUserEntity> sharedUsers = new HashSet<>();

    // Add the owner
    TripSharedUserEntity owner = new TripSharedUserEntity();
    owner.setTrip(tripEntity);
    owner.setUserId(userId);
    owner.setRole(ROLE_OWNER);
    sharedUsers.add(owner);

    // Add shared users
    if (!CollectionUtils.isEmpty(tripRequestDto.sharedUserIds())) {
      for (UUID sharedUserId : tripRequestDto.sharedUserIds()) {
        if (sharedUserId.equals(userId)) continue;
        TripSharedUserEntity sharedUser = new TripSharedUserEntity();
        sharedUser.setTrip(tripEntity);
        sharedUser.setUserId(sharedUserId);
        sharedUser.setRole(ROLE_VIEWER);
        sharedUsers.add(sharedUser);
      }
    }

    tripEntity.setSharedUsers(sharedUsers);

    // Save trip
    TripEntity savedTrip = tripService.saveTrip(tripEntity);

    return tripMapper.toTripResponseDto(savedTrip);
  }

  @Override
  public TripResponseDto getTrip(UUID userId, UUID tripId) {
    TripEntity tripEntity = tripService.getTripEntity(tripId);
    authorizeUserAccess(userId, tripEntity);
    return tripMapper.toTripResponseDto(tripEntity);
  }

  @Override
  public Page<TripResponseDto> getTrips(UUID userId, Pageable pageable) {
    Page<TripSharedUserEntity> sharedTrips =
        tripSharedUserRepository.findByUserId(userId, pageable);
    return sharedTrips.map(sharedTrip -> tripMapper.toTripResponseDto(sharedTrip.getTrip()));
  }

  @Override
  @Transactional
  public TripResponseDto updateTrip(UUID userId, UUID tripId, TripRequestDto tripRequestDto) {
    TripEntity tripEntity = tripService.getTripEntity(tripId);
    authorizeUserIsOwner(userId, tripEntity);

    // Update trip details
    tripMapper.updateTripEntityFromDto(tripRequestDto, tripEntity);

    // Update places if provided
    if (!CollectionUtils.isEmpty(tripRequestDto.placeIds())) {
      Set<PlaceEntity> places = placeService.getPlacesByIds(tripRequestDto.placeIds());
      tripEntity.setPlaces(places);
    }

    // Update shared users
    if (nonNull(tripRequestDto.sharedUserIds())) {
      updateSharedUsers(tripEntity, userId, tripRequestDto.sharedUserIds());
    }

    // Save updated trip
    TripEntity updatedTrip = tripService.saveTrip(tripEntity);

    return tripMapper.toTripResponseDto(updatedTrip);
  }

  @Override
  @Transactional
  public void deleteTrip(UUID userId, UUID tripId) {
    TripEntity tripEntity = tripService.getTripEntity(tripId);
    authorizeUserIsOwner(userId, tripEntity);
    tripService.deleteTripEntity(tripEntity);
  }

  /**
   * Handles the creation of shared users for a trip.
   *
   * @param trip Trip entity
   * @param ownerId ID of the owner
   * @param sharedUserIds Set of user IDs to share the trip with
   */
  private void handleSharedUsers(TripEntity trip, UUID ownerId, Set<UUID> sharedUserIds) {
    Set<TripSharedUserEntity> sharedUsers = new HashSet<>();

    // Add the owner
    TripSharedUserEntity owner = new TripSharedUserEntity();
    owner.setTrip(trip);
    owner.setUserId(ownerId);
    owner.setRole(ROLE_OWNER);
    sharedUsers.add(owner);

    // Add shared users
    if (!CollectionUtils.isEmpty(sharedUserIds)) {
      for (UUID userId : sharedUserIds) {
        if (userId.equals(ownerId)) continue;
        TripSharedUserEntity sharedUser = new TripSharedUserEntity();
        sharedUser.setTrip(trip);
        sharedUser.setUserId(userId);
        sharedUser.setRole(ROLE_VIEWER);
        sharedUsers.add(sharedUser);
      }
    }

    tripSharedUserRepository.saveAll(sharedUsers);
  }

  /**
   * Updates shared users for a trip.
   *
   * @param trip Trip entity
   * @param ownerId ID of the owner
   * @param sharedUserIds New set of user IDs to share the trip with
   */
  private void updateSharedUsers(TripEntity trip, UUID ownerId, Set<UUID> sharedUserIds) {
    Set<TripSharedUserEntity> sharedUsers = trip.getSharedUsers();

    // Retain only the owner
    sharedUsers.removeIf(su -> !su.getUserId().equals(ownerId));

    // Re-add shared users
    if (!CollectionUtils.isEmpty(sharedUserIds)) {
      for (UUID userId : sharedUserIds) {
        if (userId.equals(ownerId)) continue;
        boolean alreadyShared = sharedUsers.stream().anyMatch(su -> su.getUserId().equals(userId));
        if (!alreadyShared) {
          TripSharedUserEntity sharedUser = new TripSharedUserEntity();
          sharedUser.setTrip(trip);
          sharedUser.setUserId(userId);
          sharedUser.setRole(ROLE_VIEWER);
          sharedUsers.add(sharedUser);
        }
      }
    }
  }

  /**
   * Checks if the user is authorized to access the trip.
   *
   * @param userId ID of the user
   * @param tripEntity Trip entity
   * @throws AccessDeniedException if access is denied
   */
  private void authorizeUserAccess(UUID userId, TripEntity tripEntity) {
    boolean isAuthorized = tripSharedUserRepository.existsByTripAndUserId(tripEntity, userId);
    if (!isAuthorized) {
      throw new AccessDeniedException("Access denied to trip");
    }
  }

  /**
   * Checks if the user is the owner of the trip.
   *
   * @param userId ID of the user
   * @param tripEntity Trip entity
   * @throws AccessDeniedException if the user is not the owner
   */
  private void authorizeUserIsOwner(UUID userId, TripEntity tripEntity) {
    boolean isOwner =
        tripSharedUserRepository.existsByTripAndUserIdAndRole(tripEntity, userId, ROLE_OWNER);
    if (!isOwner) {
      throw new AccessDeniedException("Only the owner can perform this operation");
    }
  }
}
