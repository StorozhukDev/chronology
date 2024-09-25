package com.storozhuk.dev.chronology.trip.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.storozhuk.dev.chronology.trip.dto.api.request.PlaceRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PlaceResponseDto;
import com.storozhuk.dev.chronology.trip.entity.PlaceEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class PlaceMapperTest {

  private final PlaceMapper mapper = Mappers.getMapper(PlaceMapper.class);

  @Test
  public void testToPlaceResponseDto() {
    // Given
    PlaceEntity placeEntity = new PlaceEntity();
    placeEntity.setId(UUID.randomUUID());
    placeEntity.setName("Eiffel Tower");
    placeEntity.setCountry("France");
    placeEntity.setDescription("Iconic landmark");

    // When
    PlaceResponseDto responseDto = mapper.toPlaceResponseDto(placeEntity);

    // Then
    assertNotNull(responseDto);
    assertEquals(placeEntity.getId(), responseDto.id());
    assertEquals("Eiffel Tower", responseDto.name());
    assertEquals("France", responseDto.country());
    assertEquals("Iconic landmark", responseDto.description());
  }

  @Test
  public void testToPlaceEntity() {
    // Given
    PlaceRequestDto requestDto = new PlaceRequestDto("Louvre Museum", "France", "Famous museum");

    // When
    PlaceEntity placeEntity = mapper.toPlaceEntity(requestDto);

    // Then
    assertNotNull(placeEntity);
    assertEquals("Louvre Museum", placeEntity.getName());
    assertEquals("France", placeEntity.getCountry());
    assertEquals("Famous museum", placeEntity.getDescription());
  }

  @Test
  public void testUpdatePlaceEntityFromDto() {
    // Given
    PlaceEntity placeEntity = new PlaceEntity();
    placeEntity.setName("Old Name");
    placeEntity.setCountry("Old Country");
    placeEntity.setDescription("Old Description");

    PlaceRequestDto requestDto = new PlaceRequestDto("New Name", null, "New Description");

    // When
    mapper.updatePlaceEntityFromDto(requestDto, placeEntity);

    // Then
    assertEquals("New Name", placeEntity.getName());
    assertEquals("Old Country", placeEntity.getCountry());
    assertEquals("New Description", placeEntity.getDescription());
  }
}
