package com.storozhuk.dev.chronology.auth.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.storozhuk.dev.chronology.auth.dto.api.OAuth2UserData;
import com.storozhuk.dev.chronology.auth.dto.api.request.SignUpRequestDto;
import com.storozhuk.dev.chronology.auth.entity.RoleEntity;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import com.storozhuk.dev.chronology.lib.dto.UserInfoDto;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

public class UserMapperTest {

  private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
  private final UuidMapper uuidMapper = Mappers.getMapper(UuidMapper.class);

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(mapper, "uuidMapper", uuidMapper);
  }

  @Test
  public void testToUserInfoDto() {
    // Given
    UUID userId = UUID.randomUUID();
    UserEntity userEntity = new UserEntity();
    userEntity.setId(userId);
    userEntity.setEmail("test@example.com");
    userEntity.setName("Test User");

    RoleEntity role1 = new RoleEntity();
    role1.setName("USER");
    RoleEntity role2 = new RoleEntity();
    role2.setName("ADMIN");
    userEntity.setRoles(Set.of(role1, role2));

    // When
    UserInfoDto userInfoDto = mapper.toUserInfoDto(userEntity);

    // Then
    assertNotNull(userInfoDto);
    assertEquals(userId.toString(), userInfoDto.id());
    assertEquals("test@example.com", userInfoDto.email());
    assertEquals("Test User", userInfoDto.name());
    assertEquals(Set.of("USER", "ADMIN"), userInfoDto.roles());
  }

  @Test
  public void testToUserEntity_FromOAuth2UserData() {
    // Given
    OAuth2UserData userData =
        OAuth2UserData.builder()
            .provider("google")
            .providerUserId("123456789")
            .email("oauthuser@example.com")
            .name("OAuth User")
            .build();

    // When
    UserEntity userEntity = mapper.toUserEntity(userData);

    // Then
    assertNotNull(userEntity);
    assertNull(userEntity.getId());
    assertEquals("oauthuser@example.com", userEntity.getEmail());
    assertEquals("OAuth User", userEntity.getName());
    assertNull(userEntity.getPassword());
    assertTrue(userEntity.getRoles().isEmpty());
    assertTrue(userEntity.getSocialProviders().isEmpty());
  }

  @Test
  public void testToUserEntity_FromSignUpRequestDto() {
    // Given
    SignUpRequestDto signUpRequest =
        new SignUpRequestDto("test@example.com", "Password123", "Test User");

    // When
    UserEntity userEntity = mapper.toUserEntity(signUpRequest);

    // Then
    assertNotNull(userEntity);
    assertNull(userEntity.getId());
    assertEquals("test@example.com", userEntity.getEmail());
    assertEquals("Test User", userEntity.getName());
    assertNull(userEntity.getPassword()); // Password is ignored
    assertTrue(userEntity.getRoles().isEmpty());
    assertTrue(userEntity.getSocialProviders().isEmpty());
  }
}
