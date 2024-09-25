package com.storozhuk.dev.chronology.trip.controller;

import com.storozhuk.dev.chronology.exception.handler.dto.ErrorDto;
import com.storozhuk.dev.chronology.trip.dto.api.request.TripRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.TripResponseDto;
import com.storozhuk.dev.chronology.trip.service.facade.TripFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TripController.URL)
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Endpoints for managing trips")
public class TripController {
  public static final String URL = "/api/v1/trips";

  private final TripFacade tripFacade;

  @Operation(summary = "Create a new trip", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Trip created successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TripResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDto.class)))
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TripResponseDto createTrip(
      @AuthenticationPrincipal String userId, @Valid @RequestBody TripRequestDto tripRequestDto) {
    return tripFacade.createTrip(UUID.fromString(userId), tripRequestDto);
  }

  @Operation(summary = "Get list of trips", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trips retrieved successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = TripResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @GetMapping
  public Page<TripResponseDto> getTrips(
      @AuthenticationPrincipal String userId, @ParameterObject Pageable pageable) {
    return tripFacade.getTrips(UUID.fromString(userId), pageable);
  }

  @Operation(summary = "Get trip details", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trip retrieved successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TripResponseDto.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Trip not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @GetMapping("/{tripId}")
  public TripResponseDto getTrip(
      @AuthenticationPrincipal String userId, @PathVariable UUID tripId) {
    return tripFacade.getTrip(UUID.fromString(userId), tripId);
  }

  @Operation(summary = "Update a trip", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trip updated successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TripResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Trip not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @PutMapping("/{tripId}")
  public TripResponseDto updateTrip(
      @AuthenticationPrincipal String userId,
      @PathVariable UUID tripId,
      @Valid @RequestBody TripRequestDto tripRequestDto) {
    return tripFacade.updateTrip(UUID.fromString(userId), tripId, tripRequestDto);
  }

  @Operation(summary = "Delete a trip", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Trip deleted successfully"),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Trip not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @DeleteMapping("/{tripId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTrip(@AuthenticationPrincipal String userId, @PathVariable UUID tripId) {
    tripFacade.deleteTrip(UUID.fromString(userId), tripId);
  }
}
