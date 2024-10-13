package com.storozhuk.dev.chronology.trip.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.storozhuk.dev.chronology.trip.dto.api.request.TripRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.TripResponseDto;
import com.storozhuk.dev.chronology.trip.entity.PlaceEntity;
import com.storozhuk.dev.chronology.trip.entity.TripEntity;
import com.storozhuk.dev.chronology.trip.entity.TripSharedUserEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TripMapperTest {

  private final TripMapper mapper = Mappers.getMapper(TripMapper.class);

  @Test
  public void testToTripResponseDto() {
    // Given
    TripEntity tripEntity = new TripEntity();
    UUID tripId = UUID.randomUUID();
    tripEntity.setId(tripId);
    tripEntity.setName("Paris Trip");
    tripEntity.setStartDate(LocalDateTime.of(2023, 12, 1, 0, 0));
    tripEntity.setEndDate(LocalDateTime.of(2023, 12, 7, 0, 0));
    tripEntity.setCreatedBy(UUID.randomUUID());

    PlaceEntity place1 = new PlaceEntity();
    place1.setId(UUID.randomUUID());
    PlaceEntity place2 = new PlaceEntity();
    place2.setId(UUID.randomUUID());
    tripEntity.setPlaces(new HashSet<>(Arrays.asList(place1, place2)));

    TripSharedUserEntity user1 = new TripSharedUserEntity();
    user1.setUserId(UUID.randomUUID());
    TripSharedUserEntity user2 = new TripSharedUserEntity();
    user2.setUserId(UUID.randomUUID());
    tripEntity.setSharedUsers(new HashSet<>(Arrays.asList(user1, user2)));

    // When
    TripResponseDto responseDto = mapper.toTripResponseDto(tripEntity);

    // Then
    assertNotNull(responseDto);
    assertEquals(tripId, responseDto.id());
    assertEquals("Paris Trip", responseDto.name());
    assertEquals(tripEntity.getStartDate(), responseDto.startDate());
    assertEquals(tripEntity.getEndDate(), responseDto.endDate());
    assertEquals(2, responseDto.placeIds().size());
    assertEquals(2, responseDto.sharedUserIds().size());
  }

  @Test
  public void testToTripEntity() {
    // Given
    TripRequestDto requestDto =
        new TripRequestDto(
            "Paris Trip",
            LocalDateTime.of(2023, 12, 1, 0, 0),
            LocalDateTime.of(2023, 12, 7, 0, 0),
            Set.of(UUID.randomUUID(), UUID.randomUUID()),
            Set.of(UUID.randomUUID()));

    // When
    TripEntity tripEntity = mapper.toTripEntity(requestDto);

    // Then
    assertNotNull(tripEntity);
    assertEquals("Paris Trip", tripEntity.getName());
    assertEquals(requestDto.startDate(), tripEntity.getStartDate());
    assertEquals(requestDto.endDate(), tripEntity.getEndDate());

    // Since collections are initialized as empty sets, assert that they are empty
    assertTrue(tripEntity.getPlaces().isEmpty(), "Places should be empty");
    assertTrue(tripEntity.getSharedUsers().isEmpty(), "SharedUsers should be empty");
  }

  @Test
  public void testUpdateTripEntityFromDto() {
    // Given
    TripEntity tripEntity = new TripEntity();
    tripEntity.setName("Old Trip Name");

    TripRequestDto requestDto = new TripRequestDto("Updated Trip Name", null, null, null, null);

    // When
    mapper.updateTripEntityFromDto(requestDto, tripEntity);

    // Then
    assertEquals("Updated Trip Name", tripEntity.getName());
    // Other fields should remain unchanged
    assertNull(tripEntity.getStartDate());
    assertNull(tripEntity.getEndDate());
  }
}
