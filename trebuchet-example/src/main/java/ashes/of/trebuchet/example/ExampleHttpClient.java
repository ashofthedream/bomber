package ashes.of.trebuchet.example;

import java.util.Random;

public class ExampleHttpClient {

    private final Random random = new Random();
    private final String host;
    private final int port;

    public ExampleHttpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void requestRandom(long timeout) throws Exception {
        long ms = Math.round(timeout * 0.5 + timeout * 0.5 * random.nextDouble());
        Thread.sleep(ms);
    }

    public void requestExact(long timeout) throws Exception {
        Thread.sleep(timeout);
    }


    public void someFastRequest() throws Exception {
        try {
            Thread.sleep(50 + random.nextInt(50));
        } catch (Exception e) {

        }
    }

    public void anotherFastRequest() throws Exception {
        try {
            Thread.sleep(100 + random.nextInt(30));
        } catch (Exception e) {

        }
    }

    public void someSlowRequest() throws Exception {
        try {
            Thread.sleep(800 + random.nextInt(200));
        } catch (Exception e) {

        }
    }
}
