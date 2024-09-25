package com.storozhuk.dev.chronology.auth.dto.api.response;

public record AuthResponseDto(
    String accessToken, String refreshToken, String tokenType, long expiresIn) {}
