package com.storozhuk.dev.chronology.logger.util;

import java.util.Arrays;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

@Slf4j
@UtilityClass
public class LoggingUtil {
  public static Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final String methodName = signature.toShortString();
    final String args = Arrays.toString(joinPoint.getArgs());

    log.info("Entering: {} with args: {}", methodName, args);

    try {
      Object result = joinPoint.proceed();
      log.info("Exiting: {} with return: {}", methodName, result);
      return result;
    } catch (Throwable throwable) {
      log.error("Exception in method: {}", methodName, throwable);
      throw throwable;
    }
  }
}
