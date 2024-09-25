package com.storozhuk.dev.chronology.exception.handler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDto(
    String traceId, String errorCode, String description, List<ViolationDto> violations) {}
