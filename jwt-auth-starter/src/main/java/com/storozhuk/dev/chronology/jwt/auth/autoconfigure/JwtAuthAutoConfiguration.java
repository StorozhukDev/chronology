package com.storozhuk.dev.chronology.jwt.auth.autoconfigure;

import com.storozhuk.dev.chronology.jwt.auth.client.AuthServiceFeignClient;
import com.storozhuk.dev.chronology.jwt.auth.config.properties.AuthProperties;
import com.storozhuk.dev.chronology.jwt.auth.config.properties.JwtProperties;
import com.storozhuk.dev.chronology.jwt.auth.filter.JwtAuthenticationFilter;
import com.storozhuk.dev.chronology.jwt.auth.service.JwtTokenParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class})
@EnableFeignClients(basePackageClasses = AuthServiceFeignClient.class)
@ConfigurationPropertiesScan(
    basePackages = "com.storozhuk.dev.chronology.jwt.auth.config.properties")
public class JwtAuthAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public JwtTokenParser jwtTokenProvider(JwtProperties jwtProperties) {
    return new JwtTokenParser(jwtProperties.secretKey());
  }

  @Bean
  @ConditionalOnMissingBean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      AuthProperties authProperties,
      JwtTokenParser jwtTokenProvider,
      AuthServiceFeignClient authServiceFeignClient) {
    return new JwtAuthenticationFilter(authProperties, jwtTokenProvider, authServiceFeignClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
