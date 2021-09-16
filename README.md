### An annotation based framework for load testing

```
Bomber
    TestApp
        TestSuite
            TestCase
```

```java
@LoadTestApp
@LoadTestSuite(value = "example-test")
@LoadTestSettings(time = 30, threads = 8)
@Throttle(threshold = 10)
public class ExampleTest {
    private ExampleHttpClient client;

    /**
     * This method will be invoked once in each thread before all test methods
     */
    @BeforeTestSuite
    public void beforeSuite() throws Exception {
        client = new ExampleHttpClient("localhost", 8080);
    }

    /**
     * This method will be invoked every time before each test case
     */
    @BeforeTestCase
    public void beforeCase() throws Exception {}

    /**
     * This method will be invoked every time before each test case iteration
     */
    @BeforeEach
    public void beforeTest() throws Exception {}

    /**
     * Test case method
     */
    @LoadTestCase
    public void firstSyncTestCase() throws Exception {
        client.someSlowRequest();
    }

    /**
     * Async test case method
     * @param tools measurement tools 
     */
    @Throttle(threshold = 100)
    @LoadTestSettings(time = 60, threads = 2)
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

    /**
     * This method will be invoked every time after each test case iteration
     */
    @AfterEach
    public void afterTest() throws Exception {}

    /**
     * This method will be invoked once in each thread after each test case
     */
    @AfterTestCase
    public void afterCase() {}

    /**
     * This method will be invoked once in each thread after all test methods
     */
    @AfterTestSuite
    public void afterSuite() {
    }

    public static void main(String... args) {
        Bomber bomber = new BomberBuilder()
                // add data sinks
                .sink(new Log4jSink())
                .sink(new HistogramSink())
                .sink(new YetAnotherTimeseriesDatabaseSink())

                // create example app via builder
                .add(new TestAppBuilder()
                        .name("ExampleApp")
                        // add example test suite via annotations
                        .testSuiteClass(ExampleTest.class))
                .build()
                .start();
    }
}
```


## External Dependencies

Apache Zookeeper 3.7
```
docker run --name bomber-zookeeper -p2181:2181 -p2888:2888 -p3888:3888 -p8181:8080 -d zookeeper:3.7.0
```

