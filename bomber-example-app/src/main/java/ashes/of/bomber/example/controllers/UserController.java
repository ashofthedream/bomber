package ashes.of.bomber.example.controllers;

import ashes.of.bomber.example.model.User;
import ashes.of.bomber.example.services.MockService;
import com.sun.org.glassfish.external.statistics.Stats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static ashes.of.bomber.example.utils.RandomUtils.withProbability;
import static ashes.of.bomber.example.utils.SleepUtils.sleepQuietlyAround;

@RestController
public class UserController {
    private static final Logger log = LogManager.getLogger();

    private final MockService mockService;

    private static class Stats {
        private final String testId;
        private final AtomicLong requests = new AtomicLong();

        public Stats(String testId) {
            this.testId = testId;
        }

        public void inc() {
            requests.incrementAndGet();
        }
    }
    private AtomicReference<Stats> stats = new AtomicReference<>(new Stats("nil"));

    public UserController(MockService mockService) {
        this.mockService = mockService;

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            Stats stats = this.stats.get();
            log.info("stats: {}, count: {}", stats.testId, stats.requests.get());

        }, 1, 1, TimeUnit.SECONDS);
    }



    @GetMapping("/users/{id}/{testId}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id, @PathVariable("testId") String testId) {
        log.debug("get user: {}, testId: {}", id, testId);
        Stats stats = this.stats.updateAndGet(current -> {
            if (current.testId.equals(testId)) {
                return current;
            }
            return new Stats(testId);
        });

        stats.inc();


//        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);
//
//        if (withProbability(0.01))
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        User user = mockService.getOrCreateUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<Collection<User>> getUsers() {
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.001))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return ResponseEntity.ok(mockService.getAllUsers());
    }


}
