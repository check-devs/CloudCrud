package com.github.saintukrainian.cloudcrud.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PersonServiceAspect {

    @Before("execution(public com.github.saintukrainian.cloudcrud.entities.Person com.github.saintukrainian.cloudcrud.service.PersonService.getPersonById(int))")
    public void logMethod(JoinPoint joinPoint) {
        log.info("In {} method", joinPoint.getSignature());
    }
}
