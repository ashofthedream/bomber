package ashes.of.bomber.limiter;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {


    @Test
    public void tryPermitReturnFalseAfterPermitsIsEndsInTimePeriod() {
        RateLimiter limiter = new RateLimiter(10, Duration.ofMinutes(1));

        for (int i = 0; i < 10; i++) {
            assertTrue(limiter.tryPermit());
        }

        assertFalse(limiter.tryPermit());
    }

    @Test
    public void tryPermitReturnTrueAfterTimePeriod() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(10, Duration.ofSeconds(1));

        for (int i = 0; i < 10; i++) {
            assertTrue(limiter.tryPermit());
        }

        Thread.sleep(1_000);

        assertTrue(limiter.tryPermit());
    }

    @Test
    public void tryPermitReturnTrueAfterTimePeriodForOnePermit() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(10, Duration.ofSeconds(1));

        for (int i = 0; i < 10; i++) {
            assertTrue(limiter.tryPermit());
        }

        Thread.sleep(100);
        assertFalse(limiter.tryPermit(3));

        Thread.sleep(200);
        assertTrue(limiter.tryPermit(3));
    }

    @Test
    public void tryPermitReturnTrueAfterTimePeriodForOnePermit1() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(10, Duration.ofMillis(100));

        Map<Long, AtomicLong> map = new TreeMap<>();

        while (map.size() < 10) {
            var now = System.currentTimeMillis() / 100;
            var current = map.computeIfAbsent(now, ts -> {
                System.out.println();
                return new AtomicLong(0);
            });

            if (limiter.tryPermit())
                current.incrementAndGet();
        }

        map.forEach((time, count) -> {
            System.out.printf("%,20d -> %,20d\n", time, count.get());
            assertTrue(count.get() <= 10 * 2);
        });
    }

    @Test
    public void tryPermitsShouldBeThreadSafe() {
        var limiter = new RateLimiter(100, Duration.ofHours(1));
        var threads = Runtime.getRuntime().availableProcessors();
        var counter = new AtomicLong();
        var latch = new CountDownLatch(threads);

        var counts = new ArrayList<CompletableFuture<Long>>();
        var ex = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            var cf = CompletableFuture.supplyAsync(() -> {

                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long count = 0;
                for (int j = 0; j < 200; j++) {
                    if (limiter.tryPermit())
                        count++;
                }

                return count;
            }, ex);

            counts.add(cf);
        }

        var permits = counts.stream()
                .mapToLong(CompletableFuture::join)
                .sum();

        assertEquals(100, permits);
    }

    @Test
    public void tryPermitsShouldBeThreadSafeForLongTime() {
        var limiter = new RateLimiter(1, Duration.ofMillis(100));
        var threads = Runtime.getRuntime().availableProcessors();
        var counter = new AtomicLong();
        var latch = new CountDownLatch(threads);

        var counts = new ArrayList<CompletableFuture<Long>>();
        var ex = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            var cf = CompletableFuture.supplyAsync(() -> {

                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long count = 0;
                for (int j = 0; j < 100; j++) {
                    if (limiter.tryPermit())
                        count++;

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                return count;
            }, ex);

            counts.add(cf);
        }

        var permits = counts.stream()
                .mapToLong(CompletableFuture::join)
                .sum();

        assertTrue(permits <= Math.round((System.currentTimeMillis() - start) / 100.0 + 1));
    }
}