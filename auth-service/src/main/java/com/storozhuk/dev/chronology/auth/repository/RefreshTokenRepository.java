package com.storozhuk.dev.chronology.auth.repository;

import com.storozhuk.dev.chronology.auth.entity.RefreshTokenEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
  Optional<RefreshTokenEntity> findByToken(String token);
}
