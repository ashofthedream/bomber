package ashes.of.bomber.runner;

import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.configuration.SettingsBuilder;
import ashes.of.bomber.runner.tests.AllLifecycleMethodsTest;
import ashes.of.bomber.runner.tests.Counters;
import ashes.of.bomber.sink.Log4jSink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;


public class BuilderLifecycleTest extends LifecycleTest {
    private static final Logger log = LogManager.getLogger();

    @BeforeEach
    public void setUp() throws Exception {
        counters = new Counters();

        TestSuiteBuilder<AllLifecycleMethodsTest> suite = new TestSuiteBuilder<AllLifecycleMethodsTest>()
                .name("lifecycleAll")
                .createContext(() -> new AllLifecycleMethodsTest(counters))
                .config(config -> config
                        .warmUp(SettingsBuilder.disabled())
                        .settings(settings -> settings
                                .setSeconds(20)
                                .setThreadsCount(2)
                                .setThreadIterationsCount(10)))
                .beforeSuite(AllLifecycleMethodsTest::beforeSuite)
                .beforeSuite(true, AllLifecycleMethodsTest::beforeSuiteOnlyOnce)
                .beforeCase(AllLifecycleMethodsTest::beforeCase)
                .beforeCase(true, AllLifecycleMethodsTest::beforeCaseOnlyOnce)
                .beforeEach(AllLifecycleMethodsTest::beforeEach)
                .testCase("testA", (context, tools) -> context.testA())
                .testCase("testB", AllLifecycleMethodsTest::testB)
                .afterEach(AllLifecycleMethodsTest::afterEach)
                .afterCase(AllLifecycleMethodsTest::afterCase)
                .afterCase(true, AllLifecycleMethodsTest::afterCaseOnlyOnce)
                .afterSuite(AllLifecycleMethodsTest::afterSuite)
                .afterSuite(true, AllLifecycleMethodsTest::afterSuiteOnlyOnce);


        bomber = new BomberBuilder()
                .sink(new Log4jSink())
                .add(new TestAppBuilder()
                        .name("testAllLifecycleMethods")
                        .addSuite(suite))
                .build();
    }
}
