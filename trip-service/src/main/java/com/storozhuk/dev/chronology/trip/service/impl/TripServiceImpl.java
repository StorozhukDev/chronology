package com.storozhuk.dev.chronology.trip.service.impl;

import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;
import com.storozhuk.dev.chronology.trip.entity.TripEntity;
import com.storozhuk.dev.chronology.trip.repository.TripRepository;
import com.storozhuk.dev.chronology.trip.service.TripService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

  private final TripRepository tripRepository;

  @Override
  public TripEntity saveTrip(TripEntity tripEntity) {
    return tripRepository.save(tripEntity);
  }

  @Override
  public TripEntity getTripEntity(UUID tripId) {
    return tripRepository
        .findById(tripId)
        .orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));
  }

  @Override
  public void deleteTripEntity(TripEntity tripEntity) {
    tripRepository.delete(tripEntity);
  }
}
