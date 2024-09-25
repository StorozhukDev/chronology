package com.storozhuk.dev.chronology.auth.config.security;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.storozhuk.dev.chronology.auth.entity.RoleEntity;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.auth.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Implementation of UserDetailsService for loading user-specific data. */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Loads the user by username (email).
   *
   * @param email the email of the user
   * @return UserDetails object containing user information
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity userEntity =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User not found with email: %s".formatted(email)));

    List<SimpleGrantedAuthority> authorities =
        userEntity.getRoles().stream()
            .map(RoleEntity::getName)
            .map(SimpleGrantedAuthority::new)
            .toList();

    return new User(
        userEntity.getEmail(),
        Optional.ofNullable(userEntity.getPassword()).orElse(EMPTY),
        userEntity.isEnabled(),
        true,
        true,
        true,
        authorities);
  }
}
