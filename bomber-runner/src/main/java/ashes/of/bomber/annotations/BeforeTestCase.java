package ashes.of.bomber.annotations;

import java.lang.annotation.*;

/**
 * Marks method that will be invoked once in each thread before all test case starts
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeTestCase {

    /**
     * Indicates that this method should be invoked only once for all the threads
     */
    boolean onlyOnce() default false;
}
