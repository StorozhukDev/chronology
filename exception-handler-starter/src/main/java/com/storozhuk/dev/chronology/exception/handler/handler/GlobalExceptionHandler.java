package com.storozhuk.dev.chronology.exception.handler.handler;

import com.storozhuk.dev.chronology.exception.handler.dto.ErrorDto;
import com.storozhuk.dev.chronology.exception.handler.dto.ViolationDto;
import com.storozhuk.dev.chronology.exception.handler.exception.GenericException;
import com.storozhuk.dev.chronology.exception.handler.utils.ErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.util.MimeType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * A global exception handler that intercepts exceptions thrown by controllers and returns
 * structured error response.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDto> handleUnexpectedExceptions(Exception ex) {
    log.error("Unhandled exception occurred", ex);
    return buildResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(GenericException.class)
  public ResponseEntity<ErrorDto> handleGenericExceptions(GenericException ex) {
    log.warn("Generic exception occurred", ex);
    final List<ViolationDto> violations =
        List.of(ViolationDto.builder().details(ex.getMessage()).build());
    return buildResponseEntity(ex.getErrorCode(), ex.getHttpStatus(), violations);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorDto> handleHttpMessageNotReadableExceptions(
      HttpMessageNotReadableException ex) {
    log.warn("Syntax error in request body", ex);
    return buildResponseEntity(ErrorCode.GENERAL_SYNTAX_ERROR, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorDto> handleNoResourceFoundExceptions(NoResourceFoundException ex) {
    log.warn("No endpoint found", ex);
    final List<ViolationDto> violations =
        List.of(ViolationDto.builder().details("/%s".formatted(ex.getResourcePath())).build());
    return buildResponseEntity(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, violations);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    log.warn("Validation error", ex);
    List<ViolationDto> violations =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    ViolationDto.builder()
                        .field(fieldError.getField())
                        .violation(fieldError.getDefaultMessage())
                        .build())
            .toList();
    return buildResponseEntity(ErrorCode.VALIDATION_FAILED, HttpStatus.BAD_REQUEST, violations);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("Invalid argument", ex);
    List<ViolationDto> violations =
        List.of(ViolationDto.builder().details(ex.getMessage()).build());

    return buildResponseEntity(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, violations);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorDto> handleBadCredentialsException(BadCredentialsException ex) {
    log.warn("Invalid login attempt", ex);
    return buildResponseEntity(ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ErrorDto> handleDisabledException(DisabledException ex) {
    log.warn("Attempt to access with disabled account", ex);
    return buildResponseEntity(ErrorCode.ACCOUNT_DISABLED, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorDto> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException ex) {
    log.warn("Unsupported media type", ex);
    List<ViolationDto> violations =
        List.of(
            ViolationDto.builder()
                .details(
                    "%s media type is not supported"
                        .formatted(
                            Optional.ofNullable(ex.getContentType())
                                .map(MimeType::toString)
                                .orElse("unknown")))
                .build());

    return buildResponseEntity(
        ErrorCode.UNSUPPORTED_MEDIA_TYPE, HttpStatus.UNSUPPORTED_MEDIA_TYPE, violations);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorDto> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex) {
    log.warn("Method not allowed", ex);
    List<ViolationDto> violations =
        List.of(
            ViolationDto.builder()
                .details("%s method is not supported".formatted(ex.getMethod()))
                .build());

    return buildResponseEntity(
        ErrorCode.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED, violations);
  }

  /**
   * Builds a {@code ResponseEntity} containing the error code, message, and HTTP status.
   *
   * @param errorCode the error code
   * @param status the HTTP status code
   * @return a response entity containing the error response DTO
   */
  private ResponseEntity<ErrorDto> buildResponseEntity(ErrorCode errorCode, HttpStatus status) {
    return buildResponseEntity(errorCode, status, null);
  }

  /**
   * Builds a {@code ResponseEntity} containing the error code, message, and HTTP status.
   *
   * @param errorCode the error code
   * @param status the HTTP status code
   * @param violations the array of constraints that were violated
   * @return a response entity containing the error response DTO
   */
  private ResponseEntity<ErrorDto> buildResponseEntity(
      ErrorCode errorCode, HttpStatus status, List<ViolationDto> violations) {
    final ErrorDto errorResponse =
        new ErrorDto(currentTraceId(), errorCode.name(), errorCode.getDescription(), violations);
    return new ResponseEntity<>(errorResponse, status);
  }

  private String currentTraceId() {
    return MDC.get("traceId");
  }
}
