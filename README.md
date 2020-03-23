### An annotation based framework for load testing

```java
@Throttle(threshold = 10)
@LoadTest(time = 30, threads = 8)
@WarmUp(time = 30, threads = 1)
@LoadTestSuite(value = "example-test")
public class ExampleTest {
    private ExampleHttpClient client;

    @BeforeAll
    public void beforeAll() throws Exception {
        // This method will be invoked once in each thread before all test methods
        client = new ExampleHttpClient("localhost", 8080);
    }

    @BeforeEach
    public void beforeTest() throws Exception {
        // This method will be invoked every time before each test method
    }

    @AfterEach
    public void afterTest() throws Exception {
        // This method will be invoked every time after each test method
    }

    @AfterAll
    public void afterAll() {
        // This method will be invoked once in each thread after all test methods
    }

    @LoadTest
    public void oneSlowRequest() throws Exception {
        client.someSlowRequest();
    }

    @LoadTest
    public void twoFastRequests(Clock clock) throws Exception {
        Stopwatch fast = clock.stopwatch("someFast");
        client.someFastRequest();
        sw.success();

        Stopwatch async = clock.stopwatch("async");
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


    public static void main(String... args) {
        Report report = new TestAppBuilder()

                // add data sinks
                .sink(new Log4jSink())
                .sink(new HistogramSink())
                .sink(new YetAnotherTimeseriesDatabaseSink())

                // add example test suite via annotations
                .testSuiteClass(ExampleTest.class)
                .build()
                .run();
    }
}
```
