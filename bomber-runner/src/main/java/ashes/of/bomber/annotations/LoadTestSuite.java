package ashes.of.bomber.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


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
