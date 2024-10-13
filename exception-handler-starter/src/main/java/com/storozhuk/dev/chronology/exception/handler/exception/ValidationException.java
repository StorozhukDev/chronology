package com.storozhuk.dev.chronology.exception.handler.exception;

import com.storozhuk.dev.chronology.exception.handler.utils.ErrorCode;
import org.springframework.http.HttpStatus;

public class ValidationException extends GenericException {
  public ValidationException() {
    super(ErrorCode.VALIDATION_FAILED, HttpStatus.BAD_REQUEST);
  }

  public ValidationException(String message) {
    super(ErrorCode.VALIDATION_FAILED, HttpStatus.BAD_REQUEST, message);
  }
}
