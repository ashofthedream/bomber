package ashes.of.bomber.annotations;

import java.lang.annotation.*;

/**
 * Marks method that will be invoked once in each thread after test case finished
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterTestCase {

    /**
     * Indicates that this method should be invoked only once for all the threads
     */
    boolean onlyOnce() default false;
}
