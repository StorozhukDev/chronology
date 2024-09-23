package com.storozhuk.dev.chronology.trip.controller;

import com.storozhuk.dev.chronology.exception.handler.dto.ErrorDto;
import com.storozhuk.dev.chronology.trip.dto.api.request.PhotoRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.request.PlaceRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PhotoResponseDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PlaceResponseDto;
import com.storozhuk.dev.chronology.trip.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PlaceController.URL)
@RequiredArgsConstructor
@Tag(name = "Places", description = "Endpoints for managing places")
public class PlaceController {
  public static final String URL = "/api/v1/places";

  private final PlaceService placeService;

  @Operation(summary = "Create a new place")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Place created successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PlaceResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PlaceResponseDto createPlace(@Valid @RequestBody PlaceRequestDto placeRequestDto) {
    return placeService.createPlace(placeRequestDto);
  }

  @Operation(summary = "Get list of places")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Places retrieved successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = PlaceResponseDto.class)))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @GetMapping
  public Page<PlaceResponseDto> getPlaces(
      @RequestParam(required = false) String country,
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime endDate,
      Pageable pageable) {
    return placeService.getPlaces(country, userId, startDate, endDate, pageable);
  }

  @Operation(summary = "Get place details")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Place retrieved successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PlaceResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Place not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @GetMapping("/{placeId}")
  public PlaceResponseDto getPlace(@PathVariable UUID placeId) {
    return placeService.getPlace(placeId);
  }

  @Operation(summary = "Update a place")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Place updated successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PlaceResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Place not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @PutMapping("/{placeId}")
  public PlaceResponseDto updatePlace(
      @PathVariable UUID placeId, @Valid @RequestBody PlaceRequestDto placeRequestDto) {
    return placeService.updatePlace(placeId, placeRequestDto);
  }

  @Operation(summary = "Delete a place")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Place deleted successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "Place not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @DeleteMapping("/{placeId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePlace(@PathVariable UUID placeId) {
    placeService.deletePlace(placeId);
  }

  @Operation(summary = "Add a photo to a place")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Photo added successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PhotoResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Place not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  @PostMapping("/{placeId}/photos")
  @ResponseStatus(HttpStatus.CREATED)
  public PhotoResponseDto addPhotoToPlace(
      @PathVariable UUID placeId, @Valid @RequestBody PhotoRequestDto photoRequestDto) {
    return placeService.addPhotoToPlace(placeId, photoRequestDto);
  }
}
