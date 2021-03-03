package com.github.saintukrainian.cloudcrud.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Denys Matsenko
 * <p>
 * The {@code MeasureExecutionTime} annotation is used to highlight the method,
 * <br>
 * which execution time should be calculated.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MeasureExecutionTime {
}
