package com.storozhuk.dev.chronology.auth.service;

import com.storozhuk.dev.chronology.exception.handler.exception.AccessDeniedException;
import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;
import com.storozhuk.dev.chronology.lib.dto.UserInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for retrieving user information. */
public interface UserInfoService {

  /**
   * Retrieves user information by user ID.
   *
   * @param userId the UUID of the user as a String
   * @return the user info DTO
   * @throws NotFoundException if the user is not found
   * @throws AccessDeniedException if the account is disabled
   */
  UserInfoDto getUserInfo(String userId);

  /**
   * Retrieves user information by email.
   *
   * @param email the email of the user
   * @return the user info DTO
   * @throws NotFoundException if the user is not found
   * @throws AccessDeniedException if the account is disabled
   */
  UserInfoDto getUserInfoByEmail(String email);

  /**
   * Searches for users by a search string in email or name fields with pagination.
   *
   * @param searchString the string to search for in email or name.
   * @param pageable the pagination information.
   * @return a {@link Page} of {@link UserInfoDto}.
   */
  Page<UserInfoDto> searchUsers(String searchString, Pageable pageable);
}
