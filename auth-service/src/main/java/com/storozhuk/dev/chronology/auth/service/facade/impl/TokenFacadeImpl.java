package com.storozhuk.dev.chronology.auth.service.facade.impl;

import com.storozhuk.dev.chronology.auth.config.security.JwtTokenProvider;
import com.storozhuk.dev.chronology.auth.dto.api.request.RefreshTokenRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.response.AuthResponseDto;
import com.storozhuk.dev.chronology.auth.entity.RefreshTokenEntity;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.auth.service.RefreshTokenService;
import com.storozhuk.dev.chronology.auth.service.UserService;
import com.storozhuk.dev.chronology.auth.service.facade.TokenFacade;
import com.storozhuk.dev.chronology.exception.handler.exception.AuthorizationException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenFacadeImpl implements TokenFacade {
  private final UserService userService;
  private final RefreshTokenService refreshTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public AuthResponseDto generateTokenPair(UserEntity user) {
    String accessToken = jwtTokenProvider.generateAccessToken(user);
    String refreshToken = refreshTokenService.createRefreshToken(user.getId());

    return new AuthResponseDto(
        accessToken, refreshToken, "Bearer", jwtTokenProvider.getJwtExpiration());
  }

  @Override
  public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
    RefreshTokenEntity refreshTokenEntity =
        refreshTokenService
            .findByToken(request.refreshToken())
            .filter(token -> Instant.now().isBefore(token.getExpiryDate()))
            .orElseThrow(() -> new AuthorizationException("Invalid or expired refresh token"));
    UserEntity user = userService.getById(refreshTokenEntity.getUserId());
    refreshTokenService.deleteByToken(refreshTokenEntity.getToken());

    return generateTokenPair(user);
  }

  @Override
  public void revokeToken(RefreshTokenRequestDto request) {
    refreshTokenService.deleteByToken(request.refreshToken());
  }
}
