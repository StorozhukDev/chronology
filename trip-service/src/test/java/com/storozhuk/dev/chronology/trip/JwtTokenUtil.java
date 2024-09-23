package com.storozhuk.dev.chronology.trip;

import static com.storozhuk.dev.chronology.lib.util.JwtConstant.CLAIM_ROLES;
import static com.storozhuk.dev.chronology.lib.util.RoleConstant.USER;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import javax.crypto.SecretKey;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

  private SecretKey secretKey;

  @Value("${jwt.auth.starter.token.secretKey}")
  private String secret;

  @PostConstruct
  @SneakyThrows
  public void init() {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  public String generateToken(String userId) {
    return Jwts.builder()
        .subject(userId)
        .claim(CLAIM_ROLES, Set.of(USER))
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        .signWith(secretKey)
        .compact();
  }
}
