package com.storozhuk.dev.chronology.exception.handler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ViolationDto(String field, String violation, String details) {}
