package com.storozhuk.dev.chronology.trip.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(String[] origins) {}
