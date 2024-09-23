package com.storozhuk.dev.chronology.logger.aop;

import com.storozhuk.dev.chronology.logger.util.LoggingUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {

  @Pointcut("within(@org.springframework.stereotype.Service *)")
  public void serviceClassMethods() {}

  @Around("serviceClassMethods()")
  public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    return LoggingUtil.logMethod(joinPoint);
  }
}
