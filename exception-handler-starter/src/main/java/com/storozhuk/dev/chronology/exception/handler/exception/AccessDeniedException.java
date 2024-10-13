package com.storozhuk.dev.chronology.exception.handler.exception;

import com.storozhuk.dev.chronology.exception.handler.utils.ErrorCode;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends GenericException {
  public AccessDeniedException() {
    super(ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN);
  }

  public AccessDeniedException(String message) {
    super(ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN, message);
  }
}
