package com.storozhuk.dev.chronology.trip.repository;

import com.storozhuk.dev.chronology.trip.entity.PhotoEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {}
