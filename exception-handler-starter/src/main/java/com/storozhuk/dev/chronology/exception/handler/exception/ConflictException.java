package com.storozhuk.dev.chronology.exception.handler.exception;

import com.storozhuk.dev.chronology.exception.handler.utils.ErrorCode;
import org.springframework.http.HttpStatus;

public class ConflictException extends GenericException {
  public ConflictException() {
    super(ErrorCode.CONFLICT, HttpStatus.CONFLICT);
  }

  public ConflictException(String message) {
    super(ErrorCode.CONFLICT, HttpStatus.CONFLICT, message);
  }
}
