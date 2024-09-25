package com.storozhuk.dev.chronology.trip.service;

import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;
import com.storozhuk.dev.chronology.trip.entity.TripEntity;
import java.util.UUID;

/** Service interface for managing trips. */
public interface TripService {

  /**
   * Saves a trip entity.
   *
   * @param tripEntity the trip entity to save
   * @return the saved trip entity
   */
  TripEntity saveTrip(TripEntity tripEntity);

  /**
   * Retrieves a trip entity by its ID.
   *
   * @param tripId the UUID of the trip
   * @return the trip entity
   * @throws NotFoundException if the trip is not found
   */
  TripEntity getTripEntity(UUID tripId);

  /**
   * Deletes a trip entity.
   *
   * @param tripEntity the trip entity to delete
   */
  void deleteTripEntity(TripEntity tripEntity);
}
