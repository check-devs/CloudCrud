package com.github.saintukrainian.cloudcrud.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MeasureAspect {

    @Around("@annotation(com.github.saintukrainian.cloudcrud.annotations.MeasureExecutionTime)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        long startingPoint = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        log.info("Time taken to execute {} : {} milliseconds",
                joinPoint.getSignature().getName(), (System.currentTimeMillis() - startingPoint));
        return proceed;
    }
}
