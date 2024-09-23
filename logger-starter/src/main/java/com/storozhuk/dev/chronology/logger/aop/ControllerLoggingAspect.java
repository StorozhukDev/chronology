package com.storozhuk.dev.chronology.logger.aop;

import com.storozhuk.dev.chronology.logger.util.LoggingUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerLoggingAspect {

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerClassMethods() {}

  @Around("controllerClassMethods()")
  public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    return LoggingUtil.logMethod(joinPoint);
  }
}
