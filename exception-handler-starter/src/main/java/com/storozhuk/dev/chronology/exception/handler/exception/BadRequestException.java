package com.storozhuk.dev.chronology.exception.handler.exception;

import com.storozhuk.dev.chronology.exception.handler.utils.ErrorCode;
import org.springframework.http.HttpStatus;

public class BadRequestException extends GenericException {
  public BadRequestException() {
    super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
  }

  public BadRequestException(String message) {
    super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, message);
  }
}
