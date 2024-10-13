package com.storozhuk.dev.chronology.jwt.auth.client;

import static com.storozhuk.dev.chronology.lib.util.AuthConstant.AUTHORIZATION;

import com.storozhuk.dev.chronology.lib.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "${jwt.auth.starter.auth-service.url}")
public interface AuthServiceFeignClient {

  @GetMapping("/api/v1/userinfo/{userId}")
  UserInfoDto getUserById(
      @RequestHeader(AUTHORIZATION) String authorization, @PathVariable String userId);
}
