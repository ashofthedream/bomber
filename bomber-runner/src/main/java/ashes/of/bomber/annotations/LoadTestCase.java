package ashes.of.bomber.annotations;

import java.lang.annotation.*;


/**
 * Marks method as load test method
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoadTestCase {

    /**
     * Test name, if empty method name will be used
     */
    String value() default "";

    /**
     * Test order, all the tests will be sorted by this number, if order is equal
     */
    int order() default 0;

    /**
     * Marks test async
     */
    boolean async() default false;

    /**
     * Marks test as disabled
     */
    boolean disabled() default false;
}
