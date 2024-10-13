package com.storozhuk.dev.chronology.auth.dto.api.response;

import com.storozhuk.dev.chronology.lib.dto.UserInfoDto;
import java.util.List;

public record PageUserInfoDto(
    List<UserInfoDto> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean last) {}
