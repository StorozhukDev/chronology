package com.storozhuk.dev.chronology.exception.handler.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of error codes and their corresponding descriptions. Each error code represents a
 * specific type of error that can occur within the application.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
  INTERNAL_SERVER_ERROR("An unexpected error occurred"),
  GENERAL_SYNTAX_ERROR("General syntax error in request body"),
  UNSUPPORTED_MEDIA_TYPE("Unsupported media type"),
  METHOD_NOT_ALLOWED("Method Not Allowed"),
  CONFLICT("Resource conflict"),
  NOT_FOUND("Resource not found"),
  ACCESS_DENIED("Access denied"),
  UNAUTHORIZED("Unauthorized"),
  ACCOUNT_DISABLED("User account is disabled"),
  INVALID_CREDENTIALS("Invalid credentials"),
  BAD_REQUEST("Bad request"),
  VALIDATION_FAILED("Validation error");

  private final String description;
}
