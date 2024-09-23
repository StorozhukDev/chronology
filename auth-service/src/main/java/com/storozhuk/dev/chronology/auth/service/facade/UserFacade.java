package com.storozhuk.dev.chronology.auth.service.facade;

import com.storozhuk.dev.chronology.auth.dto.api.OAuth2UserData;
import com.storozhuk.dev.chronology.auth.dto.api.request.SignUpRequestDto;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.exception.handler.exception.ConflictException;
import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;

/** Facade interface for user-related operations, combining services and business logic. */
public interface UserFacade {

  /**
   * Retrieves a user by email.
   *
   * @param email the email of the user
   * @return the user entity
   * @throws NotFoundException if the user is not found
   */
  UserEntity getUser(String email);

  /**
   * Registers a new user with sign-up data.
   *
   * @param registrationData the sign-up request DTO
   * @return the newly created user entity
   * @throws ConflictException if a user with the same email already exists
   */
  UserEntity registerUser(SignUpRequestDto registrationData);

  /**
   * Finds or registers a user based on OAuth2 user data.
   *
   * @param userData the OAuth2 user data
   * @return the user entity
   */
  UserEntity findOrRegisterOauth2User(OAuth2UserData userData);
}
