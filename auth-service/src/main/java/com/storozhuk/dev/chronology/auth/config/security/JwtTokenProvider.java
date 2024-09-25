package com.storozhuk.dev.chronology.auth.config.security;

import static com.storozhuk.dev.chronology.lib.util.JwtConstant.CLAIM_EMAIL;
import static com.storozhuk.dev.chronology.lib.util.JwtConstant.CLAIM_ROLES;

import com.storozhuk.dev.chronology.auth.config.properties.JwtProperties;
import com.storozhuk.dev.chronology.auth.entity.RoleEntity;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Provides functionality for generating and validating JWT tokens. */
@Slf4j
@Component
public class JwtTokenProvider {
  private final JwtProperties jwtProperties;
  private final SecretKey jwtSecret;

  public JwtTokenProvider(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secretKey()));
  }

  /**
   * Generates an access token for the given user.
   *
   * @param userEntity the user entity
   * @return the generated JWT access token
   */
  public String generateAccessToken(UserEntity userEntity) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtProperties.accessTokenExpiration());

    List<String> roles =
        userEntity.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList());

    return Jwts.builder()
        .subject(userEntity.getId().toString())
        .claim(CLAIM_EMAIL, userEntity.getEmail())
        .claim(CLAIM_ROLES, roles)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(jwtSecret)
        .compact();
  }

  /**
   * Extracts the email from the JWT token.
   *
   * @param token the JWT token
   * @return the email extracted from the token
   */
  public String getEmailFromJwt(String token) {
    return Optional.ofNullable(parseClaims(token).get(CLAIM_EMAIL))
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .orElse(null);
  }

  /**
   * Validates the JWT token.
   *
   * @param token the JWT token
   * @return true if the token is valid, false otherwise
   */
  public boolean isValidToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("Invalid JWT token", e);
      return false;
    }
  }

  /**
   * Parses the claims from the JWT token.
   *
   * @param token the JWT token
   * @return the claims extracted from the token
   */
  private Claims parseClaims(String token) {
    return Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(token).getPayload();
  }

  /**
   * Retrieves the JWT access token expiration time.
   *
   * @return the expiration time in milliseconds
   */
  public long getJwtExpiration() {
    return jwtProperties.accessTokenExpiration();
  }
}
