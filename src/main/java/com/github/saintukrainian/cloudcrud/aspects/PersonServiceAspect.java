package com.github.saintukrainian.cloudcrud.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PersonServiceAspect {

    // Get methods

    @Before("execution(public com.github.saintukrainian.cloudcrud.entities.* com.github.saintukrainian.cloudcrud.service.PersonService.get*(int))")
    public void beforeGetPerson(JoinPoint joinPoint) {
        log.info("Getting entity using {} method", joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(public com.github.saintukrainian.cloudcrud.entities.* com.github.saintukrainian.cloudcrud.service.PersonService.get*(int))")
    public void afterGetPerson(JoinPoint joinPoint) {
        log.info("Found with id={}, using {} method", joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }

    @AfterThrowing("execution(public com.github.saintukrainian.cloudcrud.entities.* com.github.saintukrainian.cloudcrud.service.PersonService.get*(int))")
    public void personDetailsNotFoundLog(JoinPoint joinPoint) {
        log.error("Entity not found with id={}. Method: {}",joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }

    // Update methods


    // Delete methods

}
