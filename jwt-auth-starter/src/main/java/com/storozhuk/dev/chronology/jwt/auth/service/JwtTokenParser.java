package com.storozhuk.dev.chronology.jwt.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenParser {

  private final SecretKey secretKey;

  public JwtTokenParser(String secret) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  public String getUserIdFromJWT(String token) {
    return getClaims(token).getSubject();
  }

  public boolean isValidToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("Invalid JWT token", e);
      return false;
    }
  }

  private Claims getClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }
}
