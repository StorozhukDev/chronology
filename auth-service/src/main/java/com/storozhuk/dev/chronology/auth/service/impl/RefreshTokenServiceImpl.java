package com.storozhuk.dev.chronology.auth.service.impl;

import com.storozhuk.dev.chronology.auth.config.properties.JwtProperties;
import com.storozhuk.dev.chronology.auth.entity.RefreshTokenEntity;
import com.storozhuk.dev.chronology.auth.repository.RefreshTokenRepository;
import com.storozhuk.dev.chronology.auth.service.RefreshTokenService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtProperties jwtProperties;

  @Override
  public Optional<RefreshTokenEntity> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Override
  public String createRefreshToken(UUID userId) {
    RefreshTokenEntity refreshToken = new RefreshTokenEntity();
    refreshToken.setUserId(userId);
    refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.refreshTokenExpiration()));
    refreshToken.setToken(UUID.randomUUID().toString());
    return refreshTokenRepository.save(refreshToken).getToken();
  }

  @Override
  public void deleteByToken(String token) {
    refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
  }
}
