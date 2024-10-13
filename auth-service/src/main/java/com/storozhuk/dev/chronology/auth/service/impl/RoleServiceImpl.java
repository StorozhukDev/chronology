package com.storozhuk.dev.chronology.auth.service.impl;

import com.storozhuk.dev.chronology.auth.entity.RoleEntity;
import com.storozhuk.dev.chronology.auth.repository.RoleRepository;
import com.storozhuk.dev.chronology.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
  private final RoleRepository roleRepository;

  @Override
  public RoleEntity findOrCreateRole(String roleName) {
    return roleRepository.findByName(roleName).orElseGet(() -> createRole(roleName));
  }

  private RoleEntity createRole(String roleName) {
    RoleEntity newRole = new RoleEntity();
    newRole.setName(roleName);
    return roleRepository.save(newRole);
  }
}
