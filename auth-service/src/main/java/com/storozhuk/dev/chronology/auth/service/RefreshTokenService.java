package com.storozhuk.dev.chronology.auth.service;

import com.storozhuk.dev.chronology.auth.entity.RefreshTokenEntity;
import java.util.Optional;
import java.util.UUID;

/** Service interface for managing refresh tokens. */
public interface RefreshTokenService {

  /**
   * Creates a new refresh token for the specified user.
   *
   * @param userId the UUID of the user
   * @return the generated refresh token as a string
   */
  String createRefreshToken(UUID userId);

  /**
   * Finds a refresh token entity by its token string.
   *
   * @param token the refresh token string
   * @return an optional containing the refresh token entity if found
   */
  Optional<RefreshTokenEntity> findByToken(String token);

  /**
   * Deletes a refresh token by its token string.
   *
   * @param token the refresh token string
   */
  void deleteByToken(String token);
}
