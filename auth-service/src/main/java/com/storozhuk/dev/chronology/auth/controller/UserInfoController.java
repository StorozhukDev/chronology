package com.storozhuk.dev.chronology.auth.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.storozhuk.dev.chronology.auth.dto.api.response.PageUserInfoDto;
import com.storozhuk.dev.chronology.auth.service.UserInfoService;
import com.storozhuk.dev.chronology.exception.handler.dto.ErrorDto;
import com.storozhuk.dev.chronology.lib.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserInfoController.URL)
@RequiredArgsConstructor
@Tag(name = "User Info", description = "Endpoints for retrieving user information")
public class UserInfoController {
  public static final String URL = "/api/v1/userinfo";

  private final UserInfoService userInfoService;

  @Operation(summary = "Get user by ID", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = UserInfoDto.class)),
            description = "User retrieved successfully"),
        @ApiResponse(
            responseCode = "401",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Unauthorized"),
        @ApiResponse(
            responseCode = "404",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "User not found"),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Internal server error")
      })
  @GetMapping(value = "/{userId}", produces = APPLICATION_JSON_VALUE)
  public UserInfoDto getUserById(
      @Parameter(description = "UUID of the user", required = true) @PathVariable String userId) {
    return userInfoService.getUserInfo(userId);
  }

  @Operation(
      summary = "Get currently authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = UserInfoDto.class)),
            description = "User retrieved successfully"),
        @ApiResponse(
            responseCode = "401",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Unauthorized"),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Internal server error")
      })
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public UserInfoDto getAuthorizedUser(@AuthenticationPrincipal UserDetails userDetails) {
    return userInfoService.getUserInfoByEmail(userDetails.getUsername());
  }

  @Operation(
      summary = "Search users by search string",
      description = "Search for users by partial email or name with pagination support",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = PageUserInfoDto.class)),
            description = "Users retrieved successfully"),
        @ApiResponse(
            responseCode = "401",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Unauthorized"),
        @ApiResponse(
            responseCode = "400",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Validation error"),
        @ApiResponse(
            responseCode = "500",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)),
            description = "Internal server error")
      })
  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public Page<UserInfoDto> searchUsers(
      @Parameter(description = "Search string to search for in email or name", required = true)
          @RequestParam
          @NotBlank
          String searchString,
      @ParameterObject Pageable pageable) {
    return userInfoService.searchUsers(searchString, pageable);
  }
}
