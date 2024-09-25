package com.storozhuk.dev.chronology.jwt.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.auth.starter.token")
public record JwtProperties(String secretKey) {}
