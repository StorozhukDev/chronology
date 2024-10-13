package com.storozhuk.dev.chronology.auth.controller;

import static com.storozhuk.dev.chronology.auth.TestDataConstants.USER_ID_DISABLED;
import static com.storozhuk.dev.chronology.auth.TestDataConstants.USER_TEST_USER_ID;
import static com.storozhuk.dev.chronology.lib.util.AuthConstant.AUTHORIZATION;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.storozhuk.dev.chronology.auth.AbstractComponentTest;
import com.storozhuk.dev.chronology.auth.service.facade.TokenFacade;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class UserInfoControllerTest extends AbstractComponentTest {

  @Autowired private TokenFacade tokenFacade;

  private String accessToken;

  @BeforeEach
  public void setUp() {
    accessToken =
        tokenFacade
            .generateTokenPair(userRepository.findByEmail("testuser@example.com").orElseThrow())
            .accessToken();
  }

  @Test
  public void testGetUserById_200_Success() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/userinfo/{userId}", USER_TEST_USER_ID)
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(USER_TEST_USER_ID))
        .andExpect(jsonPath("$.email").value("testuser@example.com"))
        .andExpect(jsonPath("$.name").value("Test User with password Password123"))
        .andExpect(jsonPath("$.roles", hasItem("USER")));
  }

  @Test
  public void testGetUserById_401_Unauthorized() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/userinfo/{userId}", USER_TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  public void testGetUserById_403_AccountDisabled() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/userinfo/{userId}", USER_ID_DISABLED)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.description").value("Access denied"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(jsonPath("$.violations[0].details").value("Account disabled"));
  }

  @Test
  public void testGetUserById_404_UserNotFound() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc
        .perform(
            get("/api/v1/userinfo/{userId}", userId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
        .andExpect(jsonPath("$.description").value("Resource not found"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value("User not found with id: %s".formatted(userId.toString())));
  }

  @Test
  public void testGetUserById_400_InvalidUserIdFormat() throws Exception {
    String invalidUserId = "invalid-uuid-format";

    mockMvc
        .perform(
            get("/api/v1/userinfo/{userId}", invalidUserId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.description").value("Bad request"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value("Invalid UUID string: %s".formatted(invalidUserId)));
  }

  @Test
  public void testGetAuthorizedUser_200_Success() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/userinfo")
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("testuser@example.com"))
        .andExpect(jsonPath("$.name").value("Test User with password Password123"))
        .andExpect(jsonPath("$.roles", hasItem("USER")));
  }

  @Test
  public void testGetAuthorizedUser_401_Unauthorized() throws Exception {
    mockMvc
        .perform(get("/api/v1/userinfo").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  public void testGetAuthorizedUser_403_AccountDisabled() throws Exception {
    // Generate token for disabled user
    String disabledUserToken =
        tokenFacade
            .generateTokenPair(
                userRepository.findByEmail("testuser_disabled@example.com").orElseThrow())
            .accessToken();

    mockMvc
        .perform(
            get("/api/v1/userinfo")
                .header(AUTHORIZATION, "Bearer " + disabledUserToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
        .andExpect(jsonPath("$.description").value("Access denied"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(jsonPath("$.violations[0].details").value("Account disabled"));
  }

  @Test
  public void testSearchUsers_200_Success() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/userinfo/search")
                .param("searchString", "testuser")
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].email").value("testuser@example.com"))
        .andExpect(jsonPath("$.content[0].name").value("Test User with password Password123"))
        .andExpect(jsonPath("$.content[0].roles", hasItem("USER")));
  }

  @Test
  public void testSearchUsers_200_NoResults() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/userinfo/search")
                .param("searchString", "nonexistent")
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }

  @Test
  public void testSearchUsers_400_MissingSearchString() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/userinfo/search")
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.description").value("Bad request"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(jsonPath("$.violations[0].field").value("searchString"))
        .andExpect(jsonPath("$.violations[0].violation").value("Parameter is missing"))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(
                    "Required request parameter 'searchString' for method parameter type String is not present"));
  }

  @Test
  public void testSearchUsers_401_Unauthorized() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/userinfo/search")
                .param("searchString", "testuser")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$").doesNotExist());
  }
}
