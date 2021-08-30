package ashes.of.bomber.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Setting for load test
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface LoadTest {

    /**
     * @return load test stage time
     */
    long time() default 60;

    /**
     * @return time unit for time
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * @return number of threads
     */
    int threads() default 1;

    /**
     * @return number of total iterations for all threads
     */
    long totalIterations() default 1_000_000_000;

    /**
     * @return number of iterations per each thread.
     */
    long threadIterations() default 1_000_000_000;
}
