package com.storozhuk.dev.chronology.logger.autoconfigure;

import com.storozhuk.dev.chronology.logger.aop.ControllerLoggingAspect;
import com.storozhuk.dev.chronology.logger.aop.RepositoryLoggingAspect;
import com.storozhuk.dev.chronology.logger.aop.ServiceLoggingAspect;
import com.storozhuk.dev.chronology.logger.config.properties.LoggerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "com.storozhuk.dev.chronology.logger.config.properties")
@EnableConfigurationProperties(LoggerProperties.class)
@ConditionalOnProperty(name = "logger.enabled", havingValue = "true")
public class LoggerAutoConfiguration {
  @Bean
  public ControllerLoggingAspect controllerLoggingAspect() {
    return new ControllerLoggingAspect();
  }

  @Bean
  public ServiceLoggingAspect serviceLoggingAspect() {
    return new ServiceLoggingAspect();
  }

  @Bean
  public RepositoryLoggingAspect repositoryLoggingAspect() {
    return new RepositoryLoggingAspect();
  }
}
