package com.storozhuk.dev.chronology.auth.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.storozhuk.dev.chronology.auth.dto.api.request.LoginRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.request.RefreshTokenRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.request.SignUpRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.response.AuthResponseDto;
import com.storozhuk.dev.chronology.auth.service.AuthService;
import com.storozhuk.dev.chronology.exception.handler.dto.ErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthController.URL)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {
  public static final String URL = "/api/v1/auth";

  private final AuthService authService;

  @Operation(summary = "Sign up a new user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class)),
            description = "User registered successfully"),
        @ApiResponse(
            responseCode = "400",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Invalid input data"),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Internal server error")
      })
  @PostMapping(
      value = "/signup",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponseDto signup(@RequestBody @Valid SignUpRequestDto request) {
    return authService.register(request);
  }

  @Operation(summary = "Authenticate a user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class)),
            description = "User authenticated successfully"),
        @ApiResponse(
            responseCode = "400",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Invalid input data"),
        @ApiResponse(
            responseCode = "401",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Invalid credentials"),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Internal server error")
      })
  @PostMapping(
      value = "/login",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponseDto login(@RequestBody @Valid LoginRequestDto request) {
    return authService.authenticate(request);
  }

  @Operation(summary = "Refresh access token")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class)),
            description = "Token refreshed successfully"),
        @ApiResponse(
            responseCode = "401",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Invalid refresh token"),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Internal server error")
      })
  @PostMapping(
      value = "/refresh",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponseDto refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
    return authService.refreshToken(request);
  }

  @Operation(summary = "Revoke refresh token")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Token revoked successfully"),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Internal server error")
      })
  @PostMapping(value = "/refresh/revoke", consumes = APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void revokeRefreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
    authService.revokeToken(request);
  }
}
