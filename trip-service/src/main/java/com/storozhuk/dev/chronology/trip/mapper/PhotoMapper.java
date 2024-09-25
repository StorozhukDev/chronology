package com.storozhuk.dev.chronology.trip.mapper;

import com.storozhuk.dev.chronology.trip.dto.api.request.PhotoRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PhotoResponseDto;
import com.storozhuk.dev.chronology.trip.entity.PhotoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PhotoMapper {

  PhotoResponseDto toPhotoResponseDto(PhotoEntity photoEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "place", ignore = true)
  PhotoEntity toPhotoEntity(PhotoRequestDto photoRequestDto);
}
