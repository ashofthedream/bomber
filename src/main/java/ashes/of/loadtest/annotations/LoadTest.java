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
     * @return test name, if empty method name will be used
     */
    String value() default "";
}
