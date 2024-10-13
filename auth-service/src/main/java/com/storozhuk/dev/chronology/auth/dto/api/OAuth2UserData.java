package com.storozhuk.dev.chronology.auth.dto.api;

import lombok.Builder;

@Builder(toBuilder = true)
public record OAuth2UserData(String provider, String providerUserId, String email, String name) {}
