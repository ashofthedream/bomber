package ashes.of.bomber.runner;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.flight.Settings;
import ashes.of.bomber.tests.AllLifecycleMethodsTest;
import ashes.of.bomber.tests.Counters;
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
                .instance(() -> new AllLifecycleMethodsTest(counters))
                .warmUp(Settings::disabled)
                .settings(settings -> settings
                        .seconds(20)
                        .threadCount(2)
                        .threadIterations(10))
                .beforeSuite(AllLifecycleMethodsTest::beforeSuite)
                .beforeSuite(true, AllLifecycleMethodsTest::beforeSuiteOnlyOnce)
                .beforeCase(AllLifecycleMethodsTest::beforeCase)
                .beforeCase(true, AllLifecycleMethodsTest::beforeCaseOnlyOnce)
                .beforeEach(AllLifecycleMethodsTest::beforeEachCall)
                .testCase("testA", AllLifecycleMethodsTest::testA)
                .testCase("testB", AllLifecycleMethodsTest::testB)
                .afterEach(AllLifecycleMethodsTest::afterEachCall)
                .afterCase(AllLifecycleMethodsTest::afterCase)
                .afterCase(true, AllLifecycleMethodsTest::afterCaseOnlyOnce)
                .afterSuite(AllLifecycleMethodsTest::afterSuite)
                .afterSuite(true, AllLifecycleMethodsTest::afterSuiteOnlyOnce);

        app = new TestAppBuilder()
                .name("testAllLifecycleMethods")
//                .sink(new Log4jSink())
                .addSuite(suite)
                .build();
    }
}
