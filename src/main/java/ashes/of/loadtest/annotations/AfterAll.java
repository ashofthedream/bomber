package ashes.of.loadtest.annotations;

import java.lang.annotation.*;

/**
 * Marks method that will be invoked once in each thread after all test methods
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterAll {

    /**
     * Indicates that this method should be invoked only once for all the threads
     */
    boolean onlyOnce() default false;
}
