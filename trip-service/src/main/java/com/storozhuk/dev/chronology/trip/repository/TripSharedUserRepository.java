package com.storozhuk.dev.chronology.trip.repository;

import com.storozhuk.dev.chronology.trip.entity.TripEntity;
import com.storozhuk.dev.chronology.trip.entity.TripSharedUserEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripSharedUserRepository extends JpaRepository<TripSharedUserEntity, UUID> {
  Page<TripSharedUserEntity> findByUserId(UUID userId, Pageable pageable);

  boolean existsByTripAndUserId(TripEntity trip, UUID userId);

  boolean existsByTripAndUserIdAndRole(TripEntity trip, UUID userId, String role);

  void deleteByTripAndUserIdNot(TripEntity trip, UUID userId);
}
