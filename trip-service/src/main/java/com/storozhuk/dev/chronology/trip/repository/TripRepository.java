package com.storozhuk.dev.chronology.trip.repository;

import com.storozhuk.dev.chronology.trip.entity.TripEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface TripRepository extends JpaRepository<TripEntity, UUID> {
  @EntityGraph(attributePaths = {"places", "sharedUsers"})
  @NonNull
  Optional<TripEntity> findById(@Nullable UUID id);
}
