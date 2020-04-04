### An annotation based framework for load testing

```java
@Throttle(threshold = 10)
@LoadTest(time = 30, threads = 8)
@WarmUp(time = 30, threads = 1)
@LoadTestSuite(value = "example-test")
public class ExampleTest {
    private ExampleHttpClient client;

    @BeforeTestSuite
    public void beforeSuite() throws Exception {
        // This method will be invoked once in each thread before all test methods
        client = new ExampleHttpClient("localhost", 8080);
    }

    @BeforeTestCase
    public void beforeCase() throws Exception {
        // This method will be invoked every time before each test case
    }

    @BeforeEach
    public void beforeTest() throws Exception {
        // This method will be invoked every time before each test case iteration
    }

    @LoadTestCase
    public void firstSyncTestCase() throws Exception {
        client.someSlowRequest();
    }

    @LoadTestCase(async = true)
    public void secondAsyncTestCase(Tools tools) throws Exception {
        Stopwatch async = tools.stopwatch("async");
        for (int i = 0; i < 3; i++) {            
            client.asyncRequest()
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            async.fail(throwable);       
                        } else {
                            async.success();
                        }
                    });
        }
    }

    @AfterEach
    public void afterTest() throws Exception {
        // This method will be invoked every time after each test case iteration
    }

    @AfterTestCase
    public void afterCase() {
        // This method will be invoked once in each thread after each test case
    }

    @AfterTestSuite
    public void afterSuite() {
        // This method will be invoked once in each thread after all test methods
    }

    public static void main(String... args) {
        Report report = new TestAppBuilder()
                .name("ExampleApp")
                // add data sinks
                .sink(new Log4jSink())
                .sink(new HistogramSink())
                .sink(new YetAnotherTimeseriesDatabaseSink())

                // add example test suite via annotations
                .testSuiteClass(ExampleTest.class)
                .build()
                .start();
    }
}
```
