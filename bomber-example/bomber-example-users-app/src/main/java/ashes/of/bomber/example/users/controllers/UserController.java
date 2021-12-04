package ashes.of.bomber.example.users.controllers;

import ashes.of.bomber.example.dto.CreateUserRequest;
import ashes.of.bomber.example.dto.CreateUserResponse;
import ashes.of.bomber.example.dto.GetUsersResponse;
import ashes.of.bomber.example.models.User;
import ashes.of.bomber.example.users.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static ashes.of.bomber.example.utils.RandomUtils.withProbability;
import static ashes.of.bomber.example.utils.SleepUtils.sleepQuietlyAround;

@RestController
public class UserController {
    private static final Logger log = LogManager.getLogger();

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/create")
    public Mono<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        log.debug("create user by request: {}", request);
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.01))
            return Mono.error(new RuntimeException("Can't create user"));

        return userService.createUser(request.getUsername())
                .map(user -> new CreateUserResponse().setUser(user));
    }

    @GetMapping("/users/{id}")
    public Mono<User> getUser(@PathVariable("id") Long userId) {
        log.debug("get user: {}", userId);
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.01))
            return Mono.error(new RuntimeException("Can't get user"));

        return userService.getUser(userId);
    }

    @GetMapping("/users/{id}/all")
    public Mono<User> getUserWithAccounts(@PathVariable("id") Long userId) {
        log.debug("get user: {} with accounts", userId);
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.01))
            return Mono.error(new RuntimeException("Can't get user"));

        return userService.getUserWithAccounts(userId);
    }

    @GetMapping("/users")
    public Mono<GetUsersResponse> getUsers() {
        log.debug("get all users");
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.001))
            return Mono.error(new RuntimeException("Can't get all user"));

        return userService.getAllUsers()
                .collectList()
                .map(users -> new GetUsersResponse().setUsers(users));

    }
}
