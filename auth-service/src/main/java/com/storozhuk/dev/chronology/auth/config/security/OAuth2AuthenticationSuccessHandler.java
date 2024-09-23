package com.storozhuk.dev.chronology.auth.config.security;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storozhuk.dev.chronology.auth.dto.api.OAuth2UserData;
import com.storozhuk.dev.chronology.auth.dto.api.response.AuthResponseDto;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.auth.service.facade.TokenFacade;
import com.storozhuk.dev.chronology.auth.service.facade.UserFacade;
import com.storozhuk.dev.chronology.exception.handler.exception.AccessDeniedException;
import com.storozhuk.dev.chronology.exception.handler.exception.GenericException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/** Handles successful OAuth2 authentication and token generation. */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final TokenFacade tokenFacade;
  private final UserFacade userFacade;
  private Map<String, OAuth2UserMapper> oAuth2UserMappers;

  @PostConstruct
  private void init() {
    oAuth2UserMappers = new HashMap<>();
    oAuth2UserMappers.put(
        "google",
        (oAuth2User) ->
            OAuth2UserData.builder()
                .provider("google")
                .providerUserId(oAuth2User.getAttribute("sub"))
                .email(oAuth2User.getAttribute("email"))
                .name(oAuth2User.getAttribute("name"))
                .build());
  }

  /**
   * Handles the OAuth2 authentication success event.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param authentication the authentication object
   * @throws IOException if an input or output exception occurs
   */
  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {

    OAuth2AuthenticationToken oAuth2Authentication =
        Optional.ofNullable(authentication)
            .filter(OAuth2AuthenticationToken.class::isInstance)
            .map(OAuth2AuthenticationToken.class::cast)
            .orElseThrow(() -> new GenericException("Invalid Authentication class"));

    OAuth2User oAuth2User = oAuth2Authentication.getPrincipal();
    OAuth2UserData userData =
        Optional.ofNullable(
                oAuth2UserMappers.get(oAuth2Authentication.getAuthorizedClientRegistrationId()))
            .map(mapper -> mapper.mapOAuth2UserData(oAuth2User))
            .orElseThrow(
                () ->
                    new AccessDeniedException(
                        "Authorization unavailable with current social provider"));

    UserEntity userEntity = userFacade.findOrRegisterOauth2User(userData);

    AuthResponseDto authResponse = tokenFacade.generateTokenPair(userEntity);

    response.setContentType(APPLICATION_JSON_VALUE);
    response.getWriter().write(objectMapper.writeValueAsString(authResponse));
  }

  /** Functional interface for mapping OAuth2 user data. */
  private interface OAuth2UserMapper {
    OAuth2UserData mapOAuth2UserData(OAuth2User oAuth2User);
  }
}
