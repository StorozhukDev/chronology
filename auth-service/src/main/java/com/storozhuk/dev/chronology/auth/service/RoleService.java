package com.storozhuk.dev.chronology.auth.service;

import com.storozhuk.dev.chronology.auth.entity.RoleEntity;

/** Service interface for managing user roles. */
public interface RoleService {

  /**
   * Finds a role by its name or creates it if it doesn't exist.
   *
   * @param roleName the name of the role
   * @return the role entity
   */
  RoleEntity findOrCreateRole(String roleName);
}
