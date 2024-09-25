package com.storozhuk.dev.chronology.trip.mapper;

import com.storozhuk.dev.chronology.trip.dto.api.request.TripRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.TripResponseDto;
import com.storozhuk.dev.chronology.trip.entity.PlaceEntity;
import com.storozhuk.dev.chronology.trip.entity.TripEntity;
import com.storozhuk.dev.chronology.trip.entity.TripSharedUserEntity;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper
public interface TripMapper {

  @Mapping(target = "placeIds", source = "places", qualifiedByName = "map-places-to-ids")
  @Mapping(
      target = "sharedUserIds",
      source = "sharedUsers",
      qualifiedByName = "map-shared-users-to-ids")
  TripResponseDto toTripResponseDto(TripEntity tripEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "places", ignore = true)
  @Mapping(target = "sharedUsers", ignore = true)
  TripEntity toTripEntity(TripRequestDto tripRequestDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "places", ignore = true)
  @Mapping(target = "sharedUsers", ignore = true)
  void updateTripEntityFromDto(TripRequestDto dto, @MappingTarget TripEntity entity);

  @Named("map-places-to-ids")
  default Set<UUID> mapPlacesToIds(Set<PlaceEntity> places) {
    return Optional.ofNullable(places)
        .map(p -> p.stream().map(PlaceEntity::getId).collect(Collectors.toSet()))
        .orElse(null);
  }

  @Named("map-shared-users-to-ids")
  default Set<UUID> mapSharedUsersToIds(Set<TripSharedUserEntity> sharedUsers) {
    return Optional.ofNullable(sharedUsers)
        .map(s -> s.stream().map(TripSharedUserEntity::getUserId).collect(Collectors.toSet()))
        .orElse(null);
  }
}
