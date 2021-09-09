package ashes.of.bomber.example.controllers;

import ashes.of.bomber.example.model.User;
import ashes.of.bomber.example.services.MockService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static ashes.of.bomber.example.utils.RandomUtils.withProbability;
import static ashes.of.bomber.example.utils.SleepUtils.sleepQuietlyAround;

@RestController
public class UserController {
    private static final Logger log = LogManager.getLogger();

    private final MockService mockService;

    public UserController(MockService mockService) {
        this.mockService = mockService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        log.debug("get user: {}", id);
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.01))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

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
