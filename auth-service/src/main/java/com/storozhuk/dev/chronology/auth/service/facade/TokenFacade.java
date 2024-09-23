package com.storozhuk.dev.chronology.auth.service.facade;

import com.storozhuk.dev.chronology.auth.dto.api.request.RefreshTokenRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.response.AuthResponseDto;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.exception.handler.exception.AuthorizationException;

/** Facade interface for handling token generation and management operations. */
public interface TokenFacade {

  /**
   * Generates a pair of access and refresh tokens for the given user.
   *
   * @param user the user entity for whom to generate tokens
   * @return the authentication response DTO containing tokens and metadata
   */
  AuthResponseDto generateTokenPair(UserEntity user);

  /**
   * Refreshes the access token using a valid refresh token.
   *
   * @param request the refresh token request DTO containing the refresh token
   * @return the authentication response DTO with new tokens
   * @throws AuthorizationException if the refresh token is invalid or expired
   */
  AuthResponseDto refreshToken(RefreshTokenRequestDto request);

  /**
   * Revokes a refresh token, effectively invalidating it.
   *
   * @param request the refresh token request DTO containing the refresh token
   */
  void revokeToken(RefreshTokenRequestDto request);
}
