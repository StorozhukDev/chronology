package com.storozhuk.dev.chronology.trip.initializers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.NonNull;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class WiremockInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  @Override
  public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
    WireMockServer wireMockServer =
        new WireMockServer(
            new WireMockConfiguration().dynamicPort().usingFilesUnderClasspath("wiremock"));
    wireMockServer.start();

    applicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        applicationContext,
        "jwt.auth.starter.auth-service.url=http://localhost:%s".formatted(wireMockServer.port()));

    applicationContext.addApplicationListener(
        (ApplicationListener<ContextClosedEvent>) event -> wireMockServer.stop());
  }
}
