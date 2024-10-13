package com.storozhuk.dev.chronology.auth.repository;

import com.storozhuk.dev.chronology.auth.entity.SocialProviderEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialProviderRepository extends JpaRepository<SocialProviderEntity, UUID> {
  Optional<SocialProviderEntity> findByProviderAndProviderUserId(
      String provider, String providerUserId);
}
