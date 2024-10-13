package com.storozhuk.dev.chronology.auth.service;

import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for managing user entities. */
public interface UserService {

  /**
   * Finds a user by their email address.
   *
   * @param email the email address of the user
   * @return an optional containing the user entity if found
   */
  Optional<UserEntity> findByEmail(String email);

  /**
   * Creates a new user entity.
   *
   * @param user the user entity to create
   * @return the created user entity
   */
  UserEntity create(UserEntity user);

  /**
   * Retrieves a user by their email address.
   *
   * @param email the email address of the user
   * @return the user entity
   * @throws NotFoundException if the user is not found
   */
  UserEntity getByEmail(String email);

  /**
   * Retrieves a user by their unique identifier.
   *
   * @param userId the UUID of the user
   * @return the user entity
   * @throws NotFoundException if the user is not found
   */
  UserEntity getById(UUID userId);

  /**
   * Searches for users by a search string in email or name fields with pagination.
   *
   * @param searchString the string to search for in email or name.
   * @param pageable the pagination information.
   * @return a {@link Page} of {@link UserEntity}.
   */
  Page<UserEntity> searchUsers(String searchString, Pageable pageable);
}
