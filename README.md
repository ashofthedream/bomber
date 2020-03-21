### An annotation based framework for load testing

```java
@Throttle(threshold = 10)
@LoadTestSuite(value = "example-test", time = 30, threads = 8)
@WarmUp(disabled = true)
public class ExampleTest {
    private ExampleHttpClient client;

    @BeforeAll
    public void beforeAll() throws Exception {
        // This method will be invoked once in each thread before all test methods
        client = new ExampleHttpClient("localhost", 8080);
    }

    @BeforeLoadTest
    public void beforeTest() throws Exception {
        // This method will be invoked every time before each test method
    }

    @AfterLoadTest
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

        for (int i = 0; i < 3; i++) {
            Stopwatch another = clock.stopwatch("anotherFast");
            try {
                client.anotherFastRequest();
                another.success();
            } catch (Exception e) {
                another.fail(e);
            }
        }
    }


    public static void main(String... args) {
        new TestAppBuilder()

                // add data sinks
                .sink(new Log4jSink())
                .sink(new HistogramSink())
                .sink(new YetAnotherTimeseriesDatabaseSink())

                // add example test suite via annotations
                .testSuiteClass(ExampleTest.class)
                .build()
                .run();
    }
```
