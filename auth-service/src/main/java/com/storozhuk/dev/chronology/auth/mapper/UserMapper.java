package com.storozhuk.dev.chronology.auth.mapper;

import static java.util.Objects.nonNull;

import com.storozhuk.dev.chronology.auth.dto.api.OAuth2UserData;
import com.storozhuk.dev.chronology.auth.dto.api.request.SignUpRequestDto;
import com.storozhuk.dev.chronology.auth.entity.RoleEntity;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.lib.dto.UserInfoDto;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = UuidMapper.class)
public interface UserMapper {
  @Mapping(target = "id", source = "id", qualifiedByName = "uuid-to-string")
  UserInfoDto toUserInfoDto(UserEntity source);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "socialProviders", ignore = true)
  @Mapping(target = "password", ignore = true)
  UserEntity toUserEntity(OAuth2UserData source);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "socialProviders", ignore = true)
  @Mapping(target = "password", ignore = true)
  UserEntity toUserEntity(SignUpRequestDto source);

  default String mapRoleEntityToString(RoleEntity roleEntity) {
    return nonNull(roleEntity) ? roleEntity.getName() : null;
  }

  default Set<String> mapRoles(Set<RoleEntity> roles) {
    return roles.stream().map(this::mapRoleEntityToString).collect(Collectors.toSet());
  }
}
