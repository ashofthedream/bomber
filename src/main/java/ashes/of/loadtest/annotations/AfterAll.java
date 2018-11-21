package ashes.of.loadtest.annotations;

import java.lang.annotation.*;

/**
 * Indicates method that will be invoked once in each thread after all test methods
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterAll {
}
