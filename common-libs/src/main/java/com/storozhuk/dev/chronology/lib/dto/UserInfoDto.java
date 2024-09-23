package com.storozhuk.dev.chronology.lib.dto;

import java.util.Set;

public record UserInfoDto(String id, String email, String name, Set<String> roles) {}
