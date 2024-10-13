package com.storozhuk.dev.chronology.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storozhuk.dev.chronology.auth.initializers.TestContainerInitializer;
import com.storozhuk.dev.chronology.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = {TestContainerInitializer.class})
@ActiveProfiles("test")
@Sql(
    scripts = {"/db/data-cleanup.sql", "/db/data-init.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class AbstractComponentTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected UserRepository userRepository;
}
