package com.storozhuk.dev.chronology.trip.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.storozhuk.dev.chronology.trip.dto.api.request.PhotoRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PhotoResponseDto;
import com.storozhuk.dev.chronology.trip.entity.PhotoEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class PhotoMapperTest {

  private final PhotoMapper mapper = Mappers.getMapper(PhotoMapper.class);

  @Test
  public void testToPhotoResponseDto() {
    PhotoEntity photoEntity = new PhotoEntity();
    photoEntity.setId(UUID.randomUUID());
    photoEntity.setUrl("http://example.com/photo.jpg");

    PhotoResponseDto responseDto = mapper.toPhotoResponseDto(photoEntity);

    assertNotNull(responseDto);
    assertEquals(photoEntity.getId(), responseDto.id());
    assertEquals(photoEntity.getUrl(), responseDto.url());
  }

  @Test
  public void testToPhotoEntity() {
    PhotoRequestDto requestDto = new PhotoRequestDto("http://example.com/newphoto.jpg");

    PhotoEntity photoEntity = mapper.toPhotoEntity(requestDto);

    assertNotNull(photoEntity);
    assertEquals("http://example.com/newphoto.jpg", photoEntity.getUrl());
  }
}
