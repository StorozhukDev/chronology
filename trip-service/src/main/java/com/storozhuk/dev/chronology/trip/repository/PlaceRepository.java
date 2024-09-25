package com.storozhuk.dev.chronology.trip.repository;

import com.storozhuk.dev.chronology.trip.entity.PlaceEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface PlaceRepository
    extends JpaRepository<PlaceEntity, UUID>, JpaSpecificationExecutor<PlaceEntity> {

  @EntityGraph(attributePaths = {"photos"})
  @NonNull
  Page<PlaceEntity> findAll(Specification<PlaceEntity> spec, @Nullable Pageable pageable);
}
