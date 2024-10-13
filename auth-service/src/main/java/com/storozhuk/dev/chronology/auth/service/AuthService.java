package com.storozhuk.dev.chronology.auth.service;

import com.storozhuk.dev.chronology.auth.dto.api.request.LoginRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.request.RefreshTokenRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.request.SignUpRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.response.AuthResponseDto;
import com.storozhuk.dev.chronology.exception.handler.exception.AuthorizationException;
import com.storozhuk.dev.chronology.exception.handler.exception.ConflictException;
import org.springframework.security.authentication.BadCredentialsException;

/** Service interface for authentication and authorization operations. */
public interface AuthService {

  /**
   * Registers a new user.
   *
   * @param request the sign-up request DTO containing user details
   * @return the authentication response DTO with tokens
   * @throws ConflictException if the email is already registered
   */
  AuthResponseDto register(SignUpRequestDto request);

  /**
   * Authenticates a user.
   *
   * @param request the login request DTO containing credentials
   * @return the authentication response DTO with tokens
   * @throws BadCredentialsException if authentication fails
   */
  AuthResponseDto authenticate(LoginRequestDto request);

  /**
   * Refreshes the access token using a valid refresh token.
   *
   * @param request the refresh token request DTO
   * @return the authentication response DTO with new tokens
   * @throws AuthorizationException if the refresh token is invalid or expired
   */
  AuthResponseDto refreshToken(RefreshTokenRequestDto request);

  /**
   * Revokes a refresh token.
   *
   * @param request the refresh token request DTO
   */
  void revokeToken(RefreshTokenRequestDto request);
}
