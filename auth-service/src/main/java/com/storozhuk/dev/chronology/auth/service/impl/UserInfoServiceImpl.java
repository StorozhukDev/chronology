package com.storozhuk.dev.chronology.auth.service.impl;

import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.auth.mapper.UserMapper;
import com.storozhuk.dev.chronology.auth.service.UserInfoService;
import com.storozhuk.dev.chronology.auth.service.UserService;
import com.storozhuk.dev.chronology.exception.handler.exception.AccessDeniedException;
import com.storozhuk.dev.chronology.lib.dto.UserInfoDto;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  public UserInfoDto getUserInfo(String userId) {
    UserEntity userEntity =
        Optional.of(userService.getById(UUID.fromString(userId)))
            .filter(UserEntity::isEnabled)
            .orElseThrow(() -> new AccessDeniedException("Account disabled"));
    return userMapper.toUserInfoDto(userEntity);
  }

  @Override
  public UserInfoDto getUserInfoByEmail(String email) {
    UserEntity userEntity =
        Optional.of(userService.getByEmail(email))
            .filter(UserEntity::isEnabled)
            .orElseThrow(() -> new AccessDeniedException("Account disabled"));
    return userMapper.toUserInfoDto(userEntity);
  }

  @Override
  public Page<UserInfoDto> searchUsers(String searchString, Pageable pageable) {
    Page<UserEntity> userEntities = userService.searchUsers(searchString, pageable);
    return userEntities.map(userMapper::toUserInfoDto);
  }
}
