package com.storozhuk.dev.chronology.auth.service;

import com.storozhuk.dev.chronology.exception.handler.exception.AccessDeniedException;
import com.storozhuk.dev.chronology.exception.handler.exception.NotFoundException;
import com.storozhuk.dev.chronology.lib.dto.UserInfoDto;

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
}
