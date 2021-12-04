package ashes.of.bomber.example.users.client;

import ashes.of.bomber.example.dto.CreateUserRequest;
import ashes.of.bomber.example.dto.CreateUserResponse;
import ashes.of.bomber.example.dto.GetUsersResponse;
import ashes.of.bomber.example.models.User;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class UsersClient {

    private final WebClient webClient;

    public UsersClient(String url) {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public Mono<CreateUserResponse> createUser(CreateUserRequest request) {
        return webClient.post()
                .uri("/users/create")
                .body(BodyInserters.fromValue(request))
                .exchangeToMono(response -> {

                    if (response.statusCode().isError()) {
                        throw new RuntimeException("Invalid request: " + response.statusCode());
                    }


                    return response.bodyToMono(CreateUserResponse.class);
                });
    }

    public Mono<User> getUser(long userId) {
        return webClient.get()
                .uri("/users/{userId}", userId)
                .exchangeToMono(response -> response.bodyToMono(User.class));
    }

    public Mono<User> getUserWithAccounts(long userId) {
        return webClient.get()
                .uri("/users/{userId}/all", userId)
                .exchangeToMono(response -> response.bodyToMono(User.class));
    }


    public Mono<GetUsersResponse> getUsers() {
        return webClient.get()
                .uri("/users")
                .exchangeToMono(response -> response.bodyToMono(GetUsersResponse.class));
    }
}
