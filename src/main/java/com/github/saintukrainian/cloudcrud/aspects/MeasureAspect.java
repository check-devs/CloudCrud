package com.github.saintukrainian.cloudcrud.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Denys Matsenko
 *     <p>Simple measure time aspect
 */
@Aspect
@Component
@Slf4j
public class MeasureAspect {

  /**
   * Measure time advice
   *
   * @param joinPoint join point
   * @return proceed
   * @throws Throwable exception
   */
  @Around("@annotation(com.github.saintukrainian.cloudcrud.annotations.MeasureExecutionTime)")
  public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object proceed = joinPoint.proceed();
    long end = System.currentTimeMillis();
    log.info(
        "Time taken to execute {} : {} milliseconds",
        joinPoint.getSignature().getName(),
        (end - start));
    return proceed;
  }
}
