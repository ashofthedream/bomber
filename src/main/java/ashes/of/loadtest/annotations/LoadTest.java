package ashes.of.loadtest.annotations;

import java.lang.annotation.*;


/**
 * Marks method as load test method
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoadTest {

    /**
     * Test name, if empty method name will be used
     */
    String value() default "";

    /**
     * Marks test as disabled
     */
    boolean disabled() default false;
}
