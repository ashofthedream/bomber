package ashes.of.bomber.annotations;

import java.lang.annotation.*;


/**
 * Marks class as load test suite
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadTestSuite {

    /**
     * @return test suite name
     */
    String name() default "";

    /**
     * @return indicates that test suite will be shared around all test threads
     */
    boolean shared() default false;
}
