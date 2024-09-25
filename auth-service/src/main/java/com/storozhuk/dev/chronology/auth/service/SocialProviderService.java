package com.storozhuk.dev.chronology.auth.service;

import com.storozhuk.dev.chronology.auth.entity.SocialProviderEntity;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import java.util.Optional;

/** Service interface for managing social provider associations with users. */
public interface SocialProviderService {

  /**
   * Finds a social provider entity by provider name and provider user ID.
   *
   * @param provider the name of the social provider (e.g., "google")
   * @param providerUserId the user ID provided by the social provider
   * @return an optional containing the social provider entity if found
   */
  Optional<SocialProviderEntity> findBySocial(String provider, String providerUserId);

  /**
   * Creates a new social provider association for a user.
   *
   * @param provider the name of the social provider
   * @param providerUserId the user ID provided by the social provider
   * @param user the user entity to associate with
   * @return the created social provider entity
   */
  SocialProviderEntity createSocial(String provider, String providerUserId, UserEntity user);
}
