package com.storozhuk.dev.chronology.auth.mapper;

import static java.util.Objects.nonNull;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface UuidMapper {

  @Named("uuid-to-string")
  default String uuidToString(UUID id) {
    return nonNull(id) ? id.toString() : null;
  }
}
