package ashes.of.bomber.annotations;

import java.lang.annotation.*;

/**
 * Marks class as load test app
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadTestApp {
    Class<?>[] testSuites() default {};
}
