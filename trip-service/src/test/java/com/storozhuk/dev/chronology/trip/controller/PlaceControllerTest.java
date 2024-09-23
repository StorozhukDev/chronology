package com.storozhuk.dev.chronology.trip.controller;

import static com.storozhuk.dev.chronology.trip.TestDataConstants.PLACE_EIFFEL_TOWER_ID;
import static com.storozhuk.dev.chronology.trip.TestDataConstants.USER_TEST_USER_ID;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.storozhuk.dev.chronology.trip.AbstractComponentTest;
import com.storozhuk.dev.chronology.trip.dto.api.request.PhotoRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.request.PlaceRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class PlaceControllerTest extends AbstractComponentTest {

  private String accessToken;

  @BeforeEach
  public void setup() {
    accessToken = jwtTokenUtil.generateToken(USER_TEST_USER_ID);
  }

  @Test
  public void testCreatePlace_201_Success() throws Exception {
    PlaceRequestDto placeRequest =
        new PlaceRequestDto("Notre Dame Cathedral", "France", "Historical cathedral in Paris");

    mockMvc
        .perform(
            post("/api/v1/places")
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placeRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Notre Dame Cathedral"));
  }

  @Test
  public void testCreatePlace_400_NameNull() throws Exception {
    PlaceRequestDto placeRequest =
        new PlaceRequestDto(null, "France", "Historical cathedral in Paris");

    mockMvc
        .perform(
            post("/api/v1/places")
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placeRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations[0].field").value("name"))
        .andExpect(jsonPath("$.violations[0].violation").value("Name is required"));
  }

  @Test
  public void testCreatePlace_400_CountryNull() throws Exception {
    PlaceRequestDto placeRequest =
        new PlaceRequestDto("Notre Dame Cathedral", null, "Historical cathedral in Paris");

    mockMvc
        .perform(
            post("/api/v1/places")
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placeRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.violations[0].field").value("country"))
        .andExpect(jsonPath("$.violations[0].violation").value("Country is required"));
  }

  @Test
  public void testUpdatePlace_200_Success() throws Exception {
    PlaceRequestDto placeRequest =
        new PlaceRequestDto("Updated Name", "France", "Updated Description");

    mockMvc
        .perform(
            put("/api/v1/places/{placeId}", PLACE_EIFFEL_TOWER_ID)
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placeRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Name"))
        .andExpect(jsonPath("$.description").value("Updated Description"));
  }

  @Test
  public void testDeletePlace_204_Success() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/places/{placeId}", PLACE_EIFFEL_TOWER_ID)
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testAddPhotoToPlace_201_Success() throws Exception {
    PhotoRequestDto photoRequest = new PhotoRequestDto("http://example.com/photo.jpg");

    mockMvc
        .perform(
            post("/api/v1/places/{placeId}/photos", PLACE_EIFFEL_TOWER_ID)
                .with(bearerToken(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.url").value("http://example.com/photo.jpg"));
  }

  @Test
  public void testGetPlace_200_Success() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/places/{placeId}", PLACE_EIFFEL_TOWER_ID).with(bearerToken(accessToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(PLACE_EIFFEL_TOWER_ID))
        .andExpect(jsonPath("$.name").value("Eiffel Tower"));
  }

  @Test
  public void testGetPlaces_200_FilterByCountry() throws Exception {
    mockMvc
        .perform(get("/api/v1/places").with(bearerToken(accessToken)).param("country", "France"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[*].country", everyItem(is("France"))));
  }

  @Test
  public void testGetPlaces_200_WithDateFilter() throws Exception {
    String startDate = "2023-12-01T00:00:00";
    String endDate = "2023-12-31T23:59:59";

    mockMvc
        .perform(
            get("/api/v1/places")
                .with(bearerToken(accessToken))
                .param("startDate", startDate)
                .param("endDate", endDate))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  public void testGetPlaces_200_FilterByUser() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/places").with(bearerToken(accessToken)).param("userId", USER_TEST_USER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(4)));
  }

  @Test
  public void testGetPlaces_200_FilterByCountryAndUser() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/places")
                .with(bearerToken(accessToken))
                .param("country", "USA")
                .param("userId", USER_TEST_USER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name").value("Statue of Liberty"));
  }

  @Test
  public void testGetPlaces_200_FilterByPeriod() throws Exception {
    String startDate = "2023-10-01T00:00:00";
    String endDate = "2023-10-31T23:59:59";

    mockMvc
        .perform(
            get("/api/v1/places")
                .with(bearerToken(accessToken))
                .param("startDate", startDate)
                .param("endDate", endDate))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name").value("Great Wall of China"));
  }

  @Test
  public void testGetPlaces_200_IncludesPhotos() throws Exception {
    mockMvc
        .perform(get("/api/v1/places").with(bearerToken(accessToken)).param("country", "France"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].photos", hasSize(greaterThan(0))))
        .andExpect(jsonPath("$.content[0].photos[0].url", notNullValue()));
  }
}
