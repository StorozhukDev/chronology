package com.storozhuk.dev.chronology.auth.filter;

import static com.storozhuk.dev.chronology.lib.util.AuthConstant.AUTHORIZATION;
import static com.storozhuk.dev.chronology.lib.util.AuthConstant.BEARER_PREFIX;

import com.storozhuk.dev.chronology.auth.config.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String authorizationHeader = request.getHeader(AUTHORIZATION);
    String jwt = resolveToken(authorizationHeader);
    if (StringUtils.hasText(jwt) && jwtTokenProvider.isValidToken(jwt)) {
      String email = jwtTokenProvider.getEmailFromJwt(jwt);
      UserDetails userDetails = userDetailsService.loadUserByUsername(email);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  private String resolveToken(String bearerToken) {
    return Optional.ofNullable(bearerToken)
        .filter(bearer -> bearer.startsWith(BEARER_PREFIX))
        .map(bearer -> bearer.substring(7))
        .orElse(null);
  }
}
