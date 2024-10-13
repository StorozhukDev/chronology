package com.storozhuk.dev.chronology.exception.handler.exception;

import com.storozhuk.dev.chronology.exception.handler.utils.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundException extends GenericException {
  public NotFoundException() {
    super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND);
  }

  public NotFoundException(String message) {
    super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, message);
  }
}
