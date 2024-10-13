package com.storozhuk.dev.chronology.exception.handler.exception;

import com.storozhuk.dev.chronology.exception.handler.utils.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * The base class for all custom exceptions in the application. It extends {@code RuntimeException}
 * and includes an {@code ErrorCode} to represent specific error scenarios.
 */
@Getter
public class GenericException extends RuntimeException {

  private final ErrorCode errorCode;
  private final HttpStatus httpStatus;

  public GenericException(String message) {
    super(message);
    this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public GenericException(ErrorCode errorCode, HttpStatus httpStatus) {
    super(errorCode.getDescription());
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }

  public GenericException(ErrorCode errorCode, HttpStatus httpStatus, String message) {
    super(message);
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }
}
