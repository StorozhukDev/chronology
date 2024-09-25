package com.storozhuk.dev.chronology.trip.mapper;

import com.storozhuk.dev.chronology.trip.dto.api.request.PlaceRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PlaceResponseDto;
import com.storozhuk.dev.chronology.trip.entity.PlaceEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(uses = {PhotoMapper.class})
public interface PlaceMapper {

  @Mapping(target = "photos", source = "photos")
  PlaceResponseDto toPlaceResponseDto(PlaceEntity placeEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "photos", ignore = true)
  @Mapping(target = "trips", ignore = true)
  PlaceEntity toPlaceEntity(PlaceRequestDto placeRequestDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "photos", ignore = true)
  @Mapping(target = "trips", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updatePlaceEntityFromDto(PlaceRequestDto dto, @MappingTarget PlaceEntity entity);
}
