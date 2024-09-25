package com.storozhuk.dev.chronology.exception.handler.exception;

import com.storozhuk.dev.chronology.exception.handler.utils.ErrorCode;
import org.springframework.http.HttpStatus;

public class AuthorizationException extends GenericException {
  public AuthorizationException() {
    super(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
  }

  public AuthorizationException(String message) {
    super(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED, message);
  }
}
