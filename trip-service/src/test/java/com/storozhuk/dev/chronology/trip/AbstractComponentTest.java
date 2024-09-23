package com.storozhuk.dev.chronology.trip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.storozhuk.dev.chronology.trip.initializers.TestContainerInitializer;
import com.storozhuk.dev.chronology.trip.initializers.WiremockInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = {TestContainerInitializer.class, WiremockInitializer.class})
@ActiveProfiles("test")
@Sql(
    scripts = {"/db/data-cleanup.sql", "/db/data-init.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class AbstractComponentTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected WireMockServer wireMockServer;
  @Autowired protected JwtTokenUtil jwtTokenUtil;

  protected RequestPostProcessor bearerToken(String token) {
    return request -> {
      request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
      return request;
    };
  }
}
