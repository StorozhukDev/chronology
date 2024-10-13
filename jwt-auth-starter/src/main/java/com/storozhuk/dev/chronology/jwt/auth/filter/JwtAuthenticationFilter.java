package com.storozhuk.dev.chronology.jwt.auth.filter;

import static com.storozhuk.dev.chronology.lib.util.AuthConstant.AUTHORIZATION;
import static com.storozhuk.dev.chronology.lib.util.AuthConstant.BEARER_PREFIX;

import com.storozhuk.dev.chronology.jwt.auth.client.AuthServiceFeignClient;
import com.storozhuk.dev.chronology.jwt.auth.config.properties.AuthProperties;
import com.storozhuk.dev.chronology.jwt.auth.service.JwtTokenParser;
import com.storozhuk.dev.chronology.lib.dto.UserInfoDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final AuthProperties authProperties;
  private final JwtTokenParser jwtTokenParser;
  private final AuthServiceFeignClient authServiceFeignClient;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String authorizationHeader = request.getHeader(AUTHORIZATION);
    String jwt = resolveToken(authorizationHeader);
    if (StringUtils.hasText(jwt) && jwtTokenParser.isValidToken(jwt)) {
      String userId = jwtTokenParser.getUserIdFromJWT(jwt);
      if (authProperties.stateless()) {
        setAuthenticationFromToken(jwt, userId, request);
      } else {
        setAuthenticationFromUserInfo(authorizationHeader, userId, request);
      }
    }
    filterChain.doFilter(request, response);
  }

  private String resolveToken(String bearerToken) {
    return Optional.ofNullable(bearerToken)
        .filter(bearer -> bearer.startsWith(BEARER_PREFIX))
        .map(bearer -> bearer.substring(7))
        .orElse(null);
  }

  private void setAuthenticationFromToken(String jwt, String userId, HttpServletRequest request) {
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userId,
            null,
            jwtTokenParser.getRolesFromJWT(jwt).stream().map(SimpleGrantedAuthority::new).toList());

    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private void setAuthenticationFromUserInfo(
      String authorizationHeader, String userId, HttpServletRequest request) {
    UserInfoDto userDto = authServiceFeignClient.getUserById(authorizationHeader, userId);

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userDto.id(), null, userDto.roles().stream().map(SimpleGrantedAuthority::new).toList());

    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
