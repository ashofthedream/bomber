package ashes.of.bomber.example.controllers;

import ashes.of.bomber.example.model.User;
import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class UserController {
    private static final Logger log = LogManager.getLogger();

    private final NameGenerator nameGenerator = new NameGenerator();
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        sleepQuietlyAround(20);

        if (withProbability(0.05))
            sleepQuietlyAround(100);

        if (withProbability(0.001))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        User user = users.computeIfAbsent(id, k -> {
            Name name = nameGenerator.generateName();
            return new User(k, name.getFirstName() + " " + name.getLastName());
        });

        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<Collection<User>> getUsers() {
        sleepQuietlyAround(50);

        if (withProbability(0.05))
            sleepQuietlyAround(120);

        if (withProbability(0.001))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return ResponseEntity.ok(users.values());
    }


    private static final Random random = new Random();

    public static boolean withProbability(double probability) {
        return random.nextDouble() < probability;
    }

    public static void sleepQuietlyExact(long time) {
        sleepQuietly(time, 0.0);
    }

    public static void sleepQuietlyAround(long time) {
        sleepQuietly(time, 0.2);
    }

    public static void sleepQuietly(long time, double sp) {
        try {
            double spread = time * Math.max(0.0, Math.min(sp, 1.0));
            double timeout = time + random.nextDouble() * spread * 2 - spread;

            log.info("timeout: {}, val: {}", Math.round(timeout), spread);
            Thread.sleep(Math.round(timeout));
        } catch (InterruptedException e) {
            log.error("interrupted", e);
        }
    }
}
