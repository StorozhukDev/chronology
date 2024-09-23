package com.storozhuk.dev.chronology.auth.initializers;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainerInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
    PostgreSQLContainer<?> postgresContainer =
        new PostgreSQLContainer<>("postgres:16.4")
            .withDatabaseName("auth_test_db")
            .withUsername("postgres")
            .withPassword("postgres");
    postgresContainer.start();

    applicationContext.getBeanFactory().registerSingleton("postgresContainer", postgresContainer);

    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        applicationContext,
        "spring.datasource.url=%s".formatted(postgresContainer.getJdbcUrl()),
        "spring.datasource.username=%s".formatted(postgresContainer.getUsername()),
        "spring.datasource.password=%s".formatted(postgresContainer.getPassword()));

    applicationContext.addApplicationListener(
        (ApplicationListener<ContextClosedEvent>) event -> postgresContainer.stop());
  }
}
