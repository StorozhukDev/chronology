package com.storozhuk.dev.chronology.auth.service.impl;

import com.storozhuk.dev.chronology.auth.dto.api.request.LoginRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.request.RefreshTokenRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.request.SignUpRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.response.AuthResponseDto;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.auth.service.AuthService;
import com.storozhuk.dev.chronology.auth.service.facade.TokenFacade;
import com.storozhuk.dev.chronology.auth.service.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;

  private final TokenFacade tokenFacade;
  private final UserFacade userFacade;

  @Override
  @Transactional
  public AuthResponseDto register(SignUpRequestDto request) {
    UserEntity user = userFacade.registerUser(request);
    return tokenFacade.generateTokenPair(user);
  }

  @Override
  public AuthResponseDto authenticate(LoginRequestDto request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    UserEntity user = userFacade.getUser(request.email());
    return tokenFacade.generateTokenPair(user);
  }

  @Override
  public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
    return tokenFacade.refreshToken(request);
  }

  @Override
  public void revokeToken(RefreshTokenRequestDto request) {
    tokenFacade.revokeToken(request);
  }
}
