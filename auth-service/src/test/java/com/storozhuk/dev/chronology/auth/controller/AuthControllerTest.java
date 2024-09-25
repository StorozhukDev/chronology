package com.storozhuk.dev.chronology.auth.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.storozhuk.dev.chronology.auth.AbstractComponentTest;
import com.storozhuk.dev.chronology.auth.dto.api.request.LoginRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.request.RefreshTokenRequestDto;
import com.storozhuk.dev.chronology.auth.dto.api.request.SignUpRequestDto;
import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class AuthControllerTest extends AbstractComponentTest {

  @Test
  public void testSignup_201_Success() throws Exception {
    SignUpRequestDto signUpRequest =
        new SignUpRequestDto("newuser123@example.com", "Password123", "New User");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.refreshToken").isNotEmpty())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").isNumber());

    // Verify user is saved in the database
    UserEntity user = userRepository.findByEmail("newuser123@example.com").orElse(null);
    assertNotNull(user);
    assertEquals("New User", user.getName());
  }

  @Test
  public void testSignup_409_EmailAlreadyExists() throws Exception {
    SignUpRequestDto signUpRequest =
        new SignUpRequestDto("testuser@example.com", "Password123", "Test User");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("CONFLICT"))
        .andExpect(jsonPath("$.description").value(containsString("Resource conflict")))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(
                    containsString("User with such email already exists: testuser@example.com")));
  }

  @Test
  public void testSignup_400_MissingFields() throws Exception {
    SignUpRequestDto signUpRequest = new SignUpRequestDto(null, null, null);

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.description").value("Validation error"))
        .andExpect(jsonPath("$.violations", hasSize(3)))
        .andExpect(
            jsonPath("$.violations[*].field", containsInAnyOrder("email", "password", "name")))
        .andExpect(
            jsonPath(
                "$.violations[*].violation",
                containsInAnyOrder(
                    "Name is required", "Password is required", "Email is required")));
  }

  @Test
  public void testSignup_400_InvalidEmail() throws Exception {
    SignUpRequestDto signUpRequest =
        new SignUpRequestDto("invalid-email", "Password123", "Test User");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.description").value("Validation error"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(jsonPath("$.violations[0].field").value("email"))
        .andExpect(jsonPath("$.violations[0].violation").value("Email should be valid"));
  }

  @Test
  public void testSignup_400_ShortPassword() throws Exception {
    SignUpRequestDto signUpRequest = new SignUpRequestDto("test@example.com", "Pass1", "Test User");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.description").value("Validation error"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(jsonPath("$.violations[0].field").value("password"))
        .andExpect(
            jsonPath("$.violations[0].violation")
                .value("Password must be at least 8 characters long"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "password", // No uppercase letter or digit
        "PASSWORD", // No lowercase letter or digit
        "Password", // No digit
        "12345678", // No letters
        "passw0rd", // No uppercase letter
        "PASSWORD1", // No lowercase letter
        "Pass word1", // No spaces
      })
  public void testSignup_400_InvalidPasswordPattern(String invalidPassword) throws Exception {
    SignUpRequestDto signUpRequest =
        new SignUpRequestDto("test@example.com", invalidPassword, "Test User");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.description").value("Validation error"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(jsonPath("$.violations[0].field").value("password"))
        .andExpect(
            jsonPath("$.violations[0].violation")
                .value("Password must contain uppercase, lowercase letters, and digits"));
  }

  @Test
  public void testSignup_400_MissingName() throws Exception {
    SignUpRequestDto signUpRequest = new SignUpRequestDto("test@example.com", "Password123", null);

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.description").value("Validation error"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(jsonPath("$.violations[0].field").value("name"))
        .andExpect(jsonPath("$.violations[0].violation").value("Name is required"));
  }

  @Test
  public void testSignup_415_UnsupportedMediaType() throws Exception {
    SignUpRequestDto signUpRequest =
        new SignUpRequestDto("newuser123@example.com", "Password123", "New User");

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_XML)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
        .andExpect(jsonPath("$.description").value("Unsupported media type"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value("application/xml media type is not supported"));
  }

  @Test
  public void testSignup_415_MissingContentType() throws Exception {
    SignUpRequestDto signUpRequest =
        new SignUpRequestDto("newuser123@example.com", "Password123", "New User");

    mockMvc
        .perform(
            post("/api/v1/auth/signup").content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
        .andExpect(jsonPath("$.description").value("Unsupported media type"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("unknown media type is not supported")));
  }

  @Test
  public void testSignup_400_MalformedJson() throws Exception {
    mockMvc
        .perform(post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON).content("{.}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("GENERAL_SYNTAX_ERROR"))
        .andExpect(jsonPath("$.description").value("General syntax error in request body"));
  }

  @Test
  public void testSignup_405_MethodNotAllowed() throws Exception {
    SignUpRequestDto signUpRequest =
        new SignUpRequestDto("newuser123@example.com", "Password123", "New User");

    mockMvc
        .perform(
            get("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.errorCode").value("METHOD_NOT_ALLOWED"))
        .andExpect(jsonPath("$.description").value("Method Not Allowed"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("GET method is not supported")));
  }

  @Test
  public void testLogin_201_Success() throws Exception {
    LoginRequestDto loginRequest = new LoginRequestDto("testuser@example.com", "Password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.refreshToken").isNotEmpty())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").isNumber());
  }

  @Test
  public void testLogin_401_InvalidCredentials() throws Exception {
    LoginRequestDto loginRequest = new LoginRequestDto("nonexistent@example.com", "wrongpassword");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"))
        .andExpect(jsonPath("$.description").value(containsString("Invalid credentials")));
  }

  @Test
  public void testLogin_415_UnsupportedMediaType() throws Exception {
    LoginRequestDto loginRequest = new LoginRequestDto("testuser@example.com", "Password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_XML)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
        .andExpect(jsonPath("$.description").value("Unsupported media type"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value("application/xml media type is not supported"));
  }

  @Test
  public void testLogin_415_MissingContentType() throws Exception {
    LoginRequestDto loginRequest = new LoginRequestDto("testuser@example.com", "Password123");

    mockMvc
        .perform(post("/api/v1/auth/login").content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
        .andExpect(jsonPath("$.description").value("Unsupported media type"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("unknown media type is not supported")));
  }

  @Test
  public void testLogin_400_MalformedJson() throws Exception {
    mockMvc
        .perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content("{.}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("GENERAL_SYNTAX_ERROR"))
        .andExpect(jsonPath("$.description").value("General syntax error in request body"));
  }

  @Test
  public void testLogin_405_MethodNotAllowed() throws Exception {
    LoginRequestDto loginRequest = new LoginRequestDto("testuser@example.com", "Password123");

    mockMvc
        .perform(
            get("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.errorCode").value("METHOD_NOT_ALLOWED"))
        .andExpect(jsonPath("$.description").value("Method Not Allowed"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("GET method is not supported")));
  }

  @Test
  public void testRefreshToken_201_Success() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("valid-refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.refreshToken").isNotEmpty())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").isNumber());
  }

  @Test
  public void testRefreshToken_401_InvalidToken() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest =
        new RefreshTokenRequestDto("invalid-refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"))
        .andExpect(jsonPath("$.description").value(containsString("Unauthorized")))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("Invalid or expired refresh token")));
  }

  @Test
  public void testRefreshToken_401_ExpiredToken() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest =
        new RefreshTokenRequestDto("expired-refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"))
        .andExpect(jsonPath("$.description").value(containsString("Unauthorized")))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("Invalid or expired refresh token")));
  }

  @Test
  public void testRefreshToken_415_UnsupportedMediaType() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("valid-refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_XML)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
        .andExpect(jsonPath("$.description").value("Unsupported media type"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("application/xml media type is not supported")));
  }

  @Test
  public void testRefreshToken_415_MissingContentType() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("valid-refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
        .andExpect(jsonPath("$.description").value("Unsupported media type"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("unknown media type is not supported")));
  }

  @Test
  public void testRefreshToken_400_MalformedJson() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON).content("{.}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("GENERAL_SYNTAX_ERROR"))
        .andExpect(jsonPath("$.description").value("General syntax error in request body"));
  }

  @Test
  public void testRefreshToken_405_MethodNotAllowed() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("valid-refresh-token");

    mockMvc
        .perform(
            get("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.errorCode").value("METHOD_NOT_ALLOWED"))
        .andExpect(jsonPath("$.description").value("Method Not Allowed"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("GET method is not supported")));
  }

  @Test
  public void testRevokeRefreshToken_204_Success() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("valid-refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh/revoke")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testRevokeRefreshToken_415_UnsupportedMediaType() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("valid-refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh/revoke")
                .contentType(MediaType.APPLICATION_XML)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
        .andExpect(jsonPath("$.description").value("Unsupported media type"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("application/xml media type is not supported")));
  }

  @Test
  public void testRevokeRefreshToken_415_MissingContentType() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("valid-refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh/revoke")
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
        .andExpect(jsonPath("$.description").value("Unsupported media type"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("unknown media type is not supported")));
  }

  @Test
  public void testRevokeRefreshToken_400_MalformedJson() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/refresh/revoke")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{.}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("GENERAL_SYNTAX_ERROR"))
        .andExpect(jsonPath("$.description").value("General syntax error in request body"));
  }

  @Test
  public void testRevokeRefreshToken_405_MethodNotAllowed() throws Exception {
    RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("valid-refresh-token");

    mockMvc
        .perform(
            get("/api/v1/auth/refresh/revoke")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.errorCode").value("METHOD_NOT_ALLOWED"))
        .andExpect(jsonPath("$.description").value("Method Not Allowed"))
        .andExpect(jsonPath("$.violations", hasSize(1)))
        .andExpect(
            jsonPath("$.violations[0].details")
                .value(containsString("GET method is not supported")));
  }
}
