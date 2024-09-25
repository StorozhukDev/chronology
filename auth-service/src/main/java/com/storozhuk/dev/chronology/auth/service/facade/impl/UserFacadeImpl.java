package com.storozhuk.dev.chronology.auth.service.facade.impl;

import com.storozhuk.dev.chronology.auth.dto.api.OAuth2UserData;
import com.storozhuk.dev.chronology.auth.dto.api.request.SignUpRequestDto;
import com.storozhuk.dev.chronology.auth.entity.RoleEntity;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.auth.mapper.UserMapper;
import com.storozhuk.dev.chronology.auth.service.RoleService;
import com.storozhuk.dev.chronology.auth.service.SocialProviderService;
import com.storozhuk.dev.chronology.auth.service.UserService;
import com.storozhuk.dev.chronology.auth.service.facade.UserFacade;
import com.storozhuk.dev.chronology.exception.handler.exception.ConflictException;
import com.storozhuk.dev.chronology.lib.util.RoleConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

  private final UserMapper userMapper;
  private final UserService userService;
  private final RoleService roleService;
  private final SocialProviderService socialProviderService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserEntity getUser(String email) {
    return userService.getByEmail(email);
  }

  @Override
  @Transactional
  public UserEntity registerUser(SignUpRequestDto registrationData) {
    userService
        .findByEmail(registrationData.email())
        .ifPresent(
            user -> {
              throw new ConflictException(
                  "User with such email already exists: %s".formatted(user.getEmail()));
            });

    UserEntity newUser = userMapper.toUserEntity(registrationData);
    newUser.setPassword(passwordEncoder.encode(registrationData.password()));
    RoleEntity roleUser = roleService.findOrCreateRole(RoleConstant.USER);
    newUser.getRoles().add(roleUser);
    return userService.create(newUser);
  }

  @Override
  @Transactional
  public UserEntity findOrRegisterOauth2User(OAuth2UserData userData) {
    return socialProviderService
        .findBySocial(userData.provider(), userData.providerUserId())
        .orElseGet(
            () -> {
              UserEntity userEntity =
                  userService
                      .findByEmail(userData.email())
                      .orElseGet(
                          () -> {
                            RoleEntity roleUser = roleService.findOrCreateRole(RoleConstant.USER);
                            UserEntity newUser = userMapper.toUserEntity(userData);
                            newUser.getRoles().add(roleUser);
                            return userService.create(newUser);
                          });
              return socialProviderService.createSocial(
                  userData.provider(), userData.providerUserId(), userEntity);
            })
        .getUser();
  }
}
