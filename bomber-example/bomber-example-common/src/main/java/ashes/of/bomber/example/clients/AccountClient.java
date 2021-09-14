package ashes.of.bomber.example.clients;

import ashes.of.bomber.example.models.Account;
import ashes.of.bomber.example.models.User;
import ashes.of.bomber.example.models.requests.CreateAccountsRequest;
import ashes.of.bomber.example.models.requests.CreateAccountsResponse;
import ashes.of.bomber.example.models.requests.GetAccountsResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class AccountClient {

    private final WebClient webClient;

    public AccountClient(String url) {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public Mono<CreateAccountsResponse> createAccounts(CreateAccountsRequest request) {
        return webClient.post()
                .uri("/accounts/create")
                .body(BodyInserters.fromValue(request))
                .exchangeToMono(response -> response.bodyToMono(CreateAccountsResponse.class));
//                .doOnNext(response -> {
//                    System.out.println(response);
//                })
//                .map(CreateAccountsResponse::getAccounts);
    }

    public Mono<GetAccountsResponse> getAccountsByUser(long userId) {
        return webClient.get()
                .uri("/accounts/user/{userId}", userId)
                .exchangeToMono(response -> response.bodyToMono(GetAccountsResponse.class));
    }

    public Mono<Account> getAccount(long accountId) {
        return webClient.get()
                .uri("/accounts/{id}", accountId)
                .exchangeToMono(response -> response.bodyToMono(Account.class));
    }

    public Mono<GetAccountsResponse> getAccounts() {
        return webClient.get()
                .uri("/accounts")
                .exchangeToMono(response -> response.bodyToMono(GetAccountsResponse.class));
    }
}
