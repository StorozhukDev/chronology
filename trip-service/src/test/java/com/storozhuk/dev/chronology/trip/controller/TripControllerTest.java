package com.storozhuk.dev.chronology.trip.controller;

import static com.storozhuk.dev.chronology.trip.TestDataConstants.PLACE_EIFFEL_TOWER_ID;
import static com.storozhuk.dev.chronology.trip.TestDataConstants.TRIP_OTHER_USER_TRIP_ID;
import static com.storozhuk.dev.chronology.trip.TestDataConstants.TRIP_PARIS_VACATION_ID;
import static com.storozhuk.dev.chronology.trip.TestDataConstants.USER_TEST_USER_ID;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.storozhuk.dev.chronology.trip.AbstractComponentTest;
import com.storozhuk.dev.chronology.trip.dto.api.request.TripRequestDto;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TripControllerTest extends AbstractComponentTest {

  private String accessToken;

  @BeforeEach
  public void setup() {
    accessToken = jwtTokenUtil.generateToken(USER_TEST_USER_ID);
  }

  @Test
  public void testCreateTrip_201_Success() throws Exception {
    TripRequestDto tripRequest =
        new TripRequestDto(
            "New Trip",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(7),
            Set.of(UUID.fromString(PLACE_EIFFEL_TOWER_ID)),
            Set.of());

    mockMvc
        .perform(
            post("/api/v1/trips")
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("New Trip"))
        .andExpect(jsonPath("$.placeIds", hasSize(1)));
  }

  @Test
  public void testCreateTrip_400_MissingName() throws Exception {
    TripRequestDto tripRequest =
        new TripRequestDto(
            null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7), null, null);

    mockMvc
        .perform(
            post("/api/v1/trips")
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.description").value("Validation error"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(jsonPath("$.violations[0].field").value("name"))
        .andExpect(jsonPath("$.violations[0].violation").value("Name is required"));
  }

  @Test
  public void testCreateTrip_400_StartDateNull() throws Exception {
    TripRequestDto tripRequest =
        new TripRequestDto(
            "Trip Without Start Date", null, LocalDateTime.now().plusDays(7), null, null);

    mockMvc
        .perform(
            post("/api/v1/trips")
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.violations[0].field").value("startDate"))
        .andExpect(jsonPath("$.violations[0].violation").value("Start date is required"));
  }

  @Test
  public void testCreateTrip_400_EndDateNull() throws Exception {
    TripRequestDto tripRequest =
        new TripRequestDto(
            "Trip Without End Date", LocalDateTime.now().plusDays(1), null, null, null);

    mockMvc
        .perform(
            post("/api/v1/trips")
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.violations[0].field").value("endDate"))
        .andExpect(jsonPath("$.violations[0].violation").value("End date is required"));
  }

  @Test
  public void testCreateTrip_400_StartDateAfterEndDate() throws Exception {
    TripRequestDto tripRequest =
        new TripRequestDto(
            "Invalid Trip",
            LocalDateTime.now().plusDays(7),
            LocalDateTime.now().plusDays(1),
            null,
            null);

    mockMvc
        .perform(
            post("/api/v1/trips")
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.description").value("Validation error"))
        .andExpect(jsonPath("$.violations[0].field").value("startDate"))
        .andExpect(
            jsonPath("$.violations[0].violation").value("Start date must be before end date"));
  }

  @Test
  public void testCreateTrip_404_PlaceNotFound() throws Exception {
    UUID placeId = UUID.randomUUID();
    TripRequestDto tripRequest =
        new TripRequestDto(
            "Trip with Invalid Place",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(7),
            Set.of(placeId),
            null);

    mockMvc
        .perform(
            post("/api/v1/trips")
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
        .andExpect(jsonPath("$.description").value("Resource not found"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value("Place(s) not found with id(s): [%s]".formatted(placeId)));
  }

  @Test
  public void testCreateTrip_401_Unauthorized() throws Exception {
    TripRequestDto tripRequest =
        new TripRequestDto(
            "Trip Without Token",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(7),
            null,
            null);

    mockMvc
        .perform(
            post("/api/v1/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testUpdateTrip_200_Success() throws Exception {
    TripRequestDto tripRequest =
        new TripRequestDto(
            "Updated Trip Name",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(5),
            null,
            null);

    mockMvc
        .perform(
            put("/api/v1/trips/{tripId}", TRIP_PARIS_VACATION_ID)
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Trip Name"));
  }

  @Test
  public void testUpdateTrip_403_Forbidden() throws Exception {
    TripRequestDto tripRequest =
        new TripRequestDto(
            "Attempted Unauthorized Update",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(7),
            null,
            null);

    mockMvc
        .perform(
            put("/api/v1/trips/{tripId}", TRIP_OTHER_USER_TRIP_ID)
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripRequest)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.description").value("Access denied"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details").value("Only the owner can perform this operation"));
  }

  @Test
  public void testGetTrip_200_Success() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/trips/{tripId}", TRIP_PARIS_VACATION_ID).with(bearerToken(accessToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(TRIP_PARIS_VACATION_ID))
        .andExpect(jsonPath("$.name").value("Paris Vacation"));
  }

  @Test
  public void testGetTrip_401_Unauthorized() throws Exception {
    mockMvc
        .perform(get("/api/v1/trips/{tripId}", TRIP_PARIS_VACATION_ID))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testDeleteTrip_204_Success() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/trips/{tripId}", TRIP_PARIS_VACATION_ID).with(bearerToken(accessToken)))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testDeleteTrip_404_NotFound() throws Exception {
    String nonExistentTripId = UUID.randomUUID().toString();

    mockMvc
        .perform(delete("/api/v1/trips/{tripId}", nonExistentTripId).with(bearerToken(accessToken)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
        .andExpect(jsonPath("$.description").value("Resource not found"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value("Trip not found with id: " + nonExistentTripId));
  }
}
