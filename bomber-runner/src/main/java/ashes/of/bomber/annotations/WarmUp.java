package ashes.of.bomber.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * Enable warm-up stage and defines settings for it
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface WarmUp {

    /**
     * @return warm up stage time
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
