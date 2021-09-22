package ashes.of.bomber.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Marks method as load test method
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
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
