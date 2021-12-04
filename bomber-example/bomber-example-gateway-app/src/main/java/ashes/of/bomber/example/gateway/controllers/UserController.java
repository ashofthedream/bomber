package ashes.of.bomber.example.gateway.controllers;

import ashes.of.bomber.example.dto.CreateUserRequest;
import ashes.of.bomber.example.dto.CreateUserResponse;
import ashes.of.bomber.example.dto.GetUsersResponse;
import ashes.of.bomber.example.models.User;
import ashes.of.bomber.example.users.client.UsersClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UserController {
    private static final Logger log = LogManager.getLogger();

    private final UsersClient usersClient;

    public UserController(UsersClient usersClient) {
        this.usersClient = usersClient;
    }


    @PostMapping("/users/create")
    public Mono<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        return usersClient.createUser(request);
    }

    @GetMapping("/users/{id}")
    public Mono<User> getUser(@PathVariable("id") Long userId) {
        return usersClient.getUser(userId);
    }

    @GetMapping("/users/{id}/all")
    public Mono<User> getUserWithAccount(@PathVariable("id") Long userId) {
        return usersClient.getUserWithAccounts(userId);
    }

    @GetMapping("/users")
    public Mono<GetUsersResponse> getUsers() {
        return usersClient.getUsers();
    }
}
