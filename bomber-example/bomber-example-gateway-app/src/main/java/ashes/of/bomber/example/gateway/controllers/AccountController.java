package ashes.of.bomber.example.gateway.controllers;

import ashes.of.bomber.example.accounts.client.AccountClient;
import ashes.of.bomber.example.dto.CreateAccountsRequest;
import ashes.of.bomber.example.dto.CreateAccountsResponse;
import ashes.of.bomber.example.dto.GetAccountsResponse;
import ashes.of.bomber.example.models.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AccountController {
    private static final Logger log = LogManager.getLogger();

    private final AccountClient accountClient;

    public AccountController(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @PostMapping("/accounts/{id}")
    public Mono<CreateAccountsResponse> getAccount(CreateAccountsRequest request) {
        return accountClient.createAccounts(request);
    }

    @GetMapping("/accounts/{id}")
    public Mono<Account> getAccount(@PathVariable("id") Long id) {
        return accountClient.getAccount(id);
    }

    @GetMapping("/accounts/user/{userId}")
    public Mono<GetAccountsResponse> getAccountsByUser(@PathVariable("userId") Long userId) {
        return accountClient.getAccountsByUser(userId);
    }

    @GetMapping("/accounts")
    public Mono<GetAccountsResponse> getAccounts() {
        return accountClient.getAccounts();
    }
}
