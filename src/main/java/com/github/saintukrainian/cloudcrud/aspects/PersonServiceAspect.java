package com.github.saintukrainian.cloudcrud.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Denys Matsenko
 * <p>
 * The {@code PersonServiceAspect} class is used for logging for {@code PersonService} class
 */
@Aspect
@Component
@Slf4j
public class PersonServiceAspect {

    // Get methods

    /**
     * Method is used for logging before getSomeEntityById methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @Before("execution(public com.github.saintukrainian.cloudcrud.entities.* com.github.saintukrainian.cloudcrud.service.PersonService.get*(int))")
    public void beforeGetEntity(JoinPoint joinPoint) {
        log.info("Getting entity using {} method", joinPoint.getSignature().getName());
    }

    /**
     * Method is used for logging after getSomeEntityById methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @AfterReturning(pointcut = "execution(com.github.saintukrainian.cloudcrud.entities.* com.github.saintukrainian.cloudcrud.service.PersonService.get*(int))")
    public void afterGetEntity(JoinPoint joinPoint) {
        log.info("Found with id={}, using {} method.",
                joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }

    /**
     * Method is used for logging after throwing exceptions by getSomeEntityById methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @AfterThrowing("execution(com.github.saintukrainian.cloudcrud.entities.* com.github.saintukrainian.cloudcrud.service.PersonService.get*(int))")
    public void entityNotFoundLog(JoinPoint joinPoint) {
        log.error("Entity not found with id={}. Method: {}", joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }


    // Update methods

    /**
     * Method is used for logging before updateSomeEntity methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @Before("execution(void com.github.saintukrainian.cloudcrud.service.PersonService.update*(int, com.github.saintukrainian.cloudcrud.entities.*))")
    public void beforeUpdateEntity(JoinPoint joinPoint) {
        log.info("Updating entity using {} method", joinPoint.getSignature().getName());
    }

    /**
     * Method is used for logging after updateSomeEntity methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @AfterReturning("execution(void com.github.saintukrainian.cloudcrud.service.PersonService.update*(int, com.github.saintukrainian.cloudcrud.entities.*))")
    public void afterUpdateEntity(JoinPoint joinPoint) {
        log.info("Entity updated with id={}, using {} method", joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }

    /**
     * Method is used for logging after throwing exceptions by updateSomeEntity methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @AfterThrowing("execution(void com.github.saintukrainian.cloudcrud.service.PersonService.update*(int, com.github.saintukrainian.cloudcrud.entities.*))")
    public void entityCantBeUpdated(JoinPoint joinPoint) {
        log.error("Entity can't be updated with id={}. Method: {}", joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }


    // Delete methods

    /**
     * Method is used for logging before deleteSomeEntityById methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @Before("execution(void com.github.saintukrainian.cloudcrud.service.PersonService.delete*(int))")
    public void beforeDeleteEntity(JoinPoint joinPoint) {
        log.info("Deleting entity using {} method", joinPoint.getSignature().getName());
    }

    /**
     * Method is used for logging after deleteSomeEntityById methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @AfterReturning("execution(void com.github.saintukrainian.cloudcrud.service.PersonService.delete*(int))")
    public void afterDeleteEntity(JoinPoint joinPoint) {
        log.info("Entity deleted with id={}, using {} method", joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }

    /**
     * Method is used for logging after throwing exceptions by deleteSomeEntityById methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @AfterThrowing("execution(void com.github.saintukrainian.cloudcrud.service.PersonService.delete*(int))")
    public void entityCantBeDeleted(JoinPoint joinPoint) {
        log.error("Entity can't be deleted with id={}. Method: {}", joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }


    // Post methods

    /**
     * Method is used before saving by saveSomeEntity methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @Before("execution(void com.github.saintukrainian.cloudcrud.service.PersonService.save*(com.github.saintukrainian.cloudcrud.entities.*))")
    public void beforeSaveEntity(JoinPoint joinPoint) {
        log.info("Saving entity using {} method", joinPoint.getSignature().getName());
    }

    /**
     * Method is used after saving by saveSomeEntity methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @AfterReturning("execution(void com.github.saintukrainian.cloudcrud.service.PersonService.save*(com.github.saintukrainian.cloudcrud.entities.*))")
    public void afterSaveEntity(JoinPoint joinPoint) {
        log.info("Entity saved {}, using {} method", joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }

    /**
     * Method is used after throwing exceptions by saveSomeEntity methods
     *
     * @param joinPoint contains the whole information about method and it's params
     */
    @AfterThrowing("execution(void com.github.saintukrainian.cloudcrud.service.PersonService.save*(com.github.saintukrainian.cloudcrud.entities.*))")
    public void entityCantBeSaved(JoinPoint joinPoint) {
        log.error("Entity can't be saved {}. Method: {}", joinPoint.getArgs()[0], joinPoint.getSignature().getName());
    }

}
