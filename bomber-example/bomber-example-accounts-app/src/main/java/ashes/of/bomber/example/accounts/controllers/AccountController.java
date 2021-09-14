package ashes.of.bomber.example.accounts.controllers;

import ashes.of.bomber.example.accounts.services.AccountService;
import ashes.of.bomber.example.models.Account;
import ashes.of.bomber.example.models.requests.CreateAccountsRequest;
import ashes.of.bomber.example.models.requests.CreateAccountsResponse;
import ashes.of.bomber.example.models.requests.GetAccountsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ashes.of.bomber.example.utils.RandomUtils.withProbability;
import static ashes.of.bomber.example.utils.SleepUtils.sleepQuietlyAround;

@RestController
public class AccountController {
    private static final Logger log = LogManager.getLogger();

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/accounts/create")
    public ResponseEntity<CreateAccountsResponse> createAccount(@RequestBody CreateAccountsRequest request) {
        log.debug("create account by request: {}", request);
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.01))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        Account account = accountService.createAccount(request.getUser());
        return ResponseEntity.ok(new CreateAccountsResponse().setAccounts(List.of(account)));
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable("id") Long id) {
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.01))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        Account account = accountService.getAccount(id);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(account);
    }

    @GetMapping("/accounts/user/{userId}")
    public ResponseEntity<GetAccountsResponse> getAccountsByUser(@PathVariable("userId") Long userId) {
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.01))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        List<Account> accounts = accountService.getAccountByUser(userId);


        return ResponseEntity.ok(new GetAccountsResponse().setAccounts(accounts));
    }

    @GetMapping("/accounts")
    public ResponseEntity<GetAccountsResponse> getAccounts() {
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.001))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        List<Account> accounts = accountService.getAllAccounts()
                .stream()
                .collect(Collectors.toList());
        return ResponseEntity.ok(new GetAccountsResponse()
                .setAccounts(accounts));
    }
}
