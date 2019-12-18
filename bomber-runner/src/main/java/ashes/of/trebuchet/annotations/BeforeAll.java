package ashes.of.trebuchet.annotations;

import java.lang.annotation.*;

/**
 * Marks method that will be invoked once in each thread before all test methods
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeAll {

    /**
     * Indicates that this method should be invoked only once for all the threads
     */
    boolean onlyOnce() default false;
}
