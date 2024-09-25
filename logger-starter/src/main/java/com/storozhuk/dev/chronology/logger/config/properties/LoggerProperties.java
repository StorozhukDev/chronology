package com.storozhuk.dev.chronology.logger.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logger")
public record LoggerProperties(boolean enabled) {}
