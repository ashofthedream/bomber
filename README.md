### An annotation based framework for load testing

```java
@LoadTestCase(value = "example-test", time = 30, threads = 8)
@WarmUp(disabled = true)
@Baseline(disabled = true)
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
    public void oneSlowRequest(Stopwatch stopwatch) throws Exception {
        client.someSlowRequest();
    }

    @LoadTest
    public void twoFastRequests(Stopwatch stopwatch) throws Exception {
        client.someFastRequest();
        stopwatch.elapsed("someFastRequest");
        client.anotherFastRequest();
        stopwatch.elapsed("anotherFastRequest");
    }


    public static void main(String... args) {
        new TestSuite()

                // add data sinks
                .sink(new Log4jSink())
                .sink(new HistogramSink())
                .sink(new YetAnotherTimeseriesDatabaseSink())

                // add example test case via annotations
                .testCase(ExampleTest.class)

                // run all of tests
                .run();
    }
```