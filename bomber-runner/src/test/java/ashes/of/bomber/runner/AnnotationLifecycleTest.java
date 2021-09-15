package ashes.of.bomber.runner;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.runner.tests.AllLifecycleMethodsTest;
import ashes.of.bomber.runner.tests.Counters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;


public class AnnotationLifecycleTest extends LifecycleTest {
    private static final Logger log = LogManager.getLogger();

    @BeforeEach
    public void setUp() {
        counters = new Counters();
        app = new TestAppBuilder()
                .name("testAllLifecycleMethods")
                .provide(Counters.class, () -> counters)
//                .sink(new Log4jSink())
                .testSuiteClass(AllLifecycleMethodsTest.class)
                .build();
    }
}
