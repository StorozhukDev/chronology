package com.storozhuk.dev.chronology.auth.service.impl;

import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.auth.repository.UserRepository;
import com.storozhuk.dev.chronology.auth.service.UserService;
import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public Optional<UserEntity> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public UserEntity create(UserEntity user) {
    return userRepository.save(user);
  }

  @Override
  public UserEntity getByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User not found with email: %s".formatted(email)));
  }

  @Override
  public UserEntity getById(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new NotFoundException("User not found with id: %s".formatted(userId)));
  }
}
