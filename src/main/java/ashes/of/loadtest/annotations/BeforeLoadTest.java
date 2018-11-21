package ashes.of.loadtest.annotations;

import java.lang.annotation.*;

/**
 * Indicates method that will be invoked every time before each test method invocation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeLoadTest {
}
