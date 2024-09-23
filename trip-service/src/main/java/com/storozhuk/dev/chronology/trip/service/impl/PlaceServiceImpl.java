package com.storozhuk.dev.chronology.trip.service.impl;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.anyNotNull;
import static org.apache.commons.lang3.ObjectUtils.notEqual;

import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;
import com.storozhuk.dev.chronology.trip.dto.api.request.PhotoRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.request.PlaceRequestDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PhotoResponseDto;
import com.storozhuk.dev.chronology.trip.dto.api.response.PlaceResponseDto;
import com.storozhuk.dev.chronology.trip.entity.PhotoEntity;
import com.storozhuk.dev.chronology.trip.entity.PlaceEntity;
import com.storozhuk.dev.chronology.trip.entity.TripEntity;
import com.storozhuk.dev.chronology.trip.mapper.PhotoMapper;
import com.storozhuk.dev.chronology.trip.mapper.PlaceMapper;
import com.storozhuk.dev.chronology.trip.repository.PhotoRepository;
import com.storozhuk.dev.chronology.trip.repository.PlaceRepository;
import com.storozhuk.dev.chronology.trip.service.PlaceService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

  private final PlaceRepository placeRepository;
  private final PhotoRepository photoRepository;
  private final PlaceMapper placeMapper;
  private final PhotoMapper photoMapper;

  @Override
  public PlaceResponseDto createPlace(PlaceRequestDto placeRequestDto) {
    PlaceEntity placeEntity = placeMapper.toPlaceEntity(placeRequestDto);
    PlaceEntity savedPlace = placeRepository.save(placeEntity);
    return placeMapper.toPlaceResponseDto(savedPlace);
  }

  @Override
  public PlaceResponseDto getPlace(UUID placeId) {
    PlaceEntity placeEntity =
        placeRepository
            .findById(placeId)
            .orElseThrow(() -> new NotFoundException("Place not found with id: " + placeId));
    return placeMapper.toPlaceResponseDto(placeEntity);
  }

  @Override
  public Page<PlaceResponseDto> getPlaces(
      String country,
      UUID userId,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Pageable pageable) {

    Specification<PlaceEntity> spec = Specification.where(null);

    if (StringUtils.hasText(country)) {
      spec =
          spec.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("country"), country));
    }

    if (anyNotNull(userId, startDate, endDate)) {
      spec =
          spec.and(
              (root, query, criteriaBuilder) -> {
                root.fetch("trips", JoinType.LEFT).fetch("sharedUsers", JoinType.LEFT);
                Join<PlaceEntity, TripEntity> tripJoin = root.join("trips", JoinType.LEFT);

                Predicate predicate = criteriaBuilder.conjunction();

                if (nonNull(userId)) {
                  predicate =
                      criteriaBuilder.and(
                          predicate,
                          criteriaBuilder.equal(
                              tripJoin.join("sharedUsers", JoinType.LEFT).get("userId"), userId));
                }

                if (nonNull(startDate)) {
                  predicate =
                      criteriaBuilder.and(
                          predicate,
                          criteriaBuilder.greaterThanOrEqualTo(
                              tripJoin.get("startDate"), startDate));
                }

                if (nonNull(endDate)) {
                  predicate =
                      criteriaBuilder.and(
                          predicate,
                          criteriaBuilder.lessThanOrEqualTo(tripJoin.get("endDate"), endDate));
                }

                return predicate;
              });
    }

    Page<PlaceEntity> placeEntities = placeRepository.findAll(spec, pageable);
    return placeEntities.map(placeMapper::toPlaceResponseDto);
  }

  @Override
  public Set<PlaceEntity> getPlacesByIds(Set<UUID> placeIds) {
    if (CollectionUtils.isEmpty(placeIds)) {
      return Collections.emptySet();
    }

    // Fetch all places by IDs
    Set<PlaceEntity> places = new HashSet<>(placeRepository.findAllById(placeIds));

    // Check if the number of fetched places is less than the number of requested IDs
    if (notEqual(places.size(), placeIds.size())) {
      // Identify the missing IDs
      Set<UUID> foundIds = places.stream().map(PlaceEntity::getId).collect(Collectors.toSet());
      Set<UUID> missingIds = new HashSet<>(placeIds);
      missingIds.removeAll(foundIds);

      // Throw an exception for the missing IDs
      throw new NotFoundException("Place(s) not found with id(s): " + missingIds);
    }

    return places;
  }

  @Override
  public PlaceResponseDto updatePlace(UUID placeId, PlaceRequestDto placeRequestDto) {
    PlaceEntity placeEntity =
        placeRepository
            .findById(placeId)
            .orElseThrow(() -> new NotFoundException("Place not found with id: " + placeId));

    placeMapper.updatePlaceEntityFromDto(placeRequestDto, placeEntity);
    PlaceEntity updatedPlace = placeRepository.save(placeEntity);
    return placeMapper.toPlaceResponseDto(updatedPlace);
  }

  @Override
  @Transactional
  public void deletePlace(UUID placeId) {
    PlaceEntity placeEntity =
        placeRepository
            .findById(placeId)
            .orElseThrow(() -> new NotFoundException("Place not found with id: " + placeId));

    for (TripEntity trip : placeEntity.getTrips()) {
      trip.getPlaces().remove(placeEntity);
    }

    placeRepository.delete(placeEntity);
  }

  @Override
  @Transactional
  public PhotoResponseDto addPhotoToPlace(UUID placeId, PhotoRequestDto photoRequestDto) {
    PlaceEntity placeEntity =
        placeRepository
            .findById(placeId)
            .orElseThrow(() -> new NotFoundException("Place not found with id: " + placeId));

    PhotoEntity photoEntity = photoMapper.toPhotoEntity(photoRequestDto);
    photoEntity.setPlace(placeEntity);

    PhotoEntity savedPhoto = photoRepository.save(photoEntity);
    placeEntity.getPhotos().add(savedPhoto);

    return photoMapper.toPhotoResponseDto(savedPhoto);
  }
}
