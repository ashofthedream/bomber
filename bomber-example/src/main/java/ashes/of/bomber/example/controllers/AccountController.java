package ashes.of.bomber.example.controllers;

import ashes.of.bomber.example.model.Account;
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
public class AccountController {
    private static final Logger log = LogManager.getLogger();

    private final MockService mockService;

    public AccountController(MockService mockService) {
        this.mockService = mockService;
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable("id") Long id) {
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.01))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        Account account = mockService.getOrCreateAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/accounts")
    public ResponseEntity<Collection<Account>> getAccounts() {
        sleepQuietlyAround(withProbability(0.05) ? 100 : 20);

        if (withProbability(0.001))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        Collection<Account> accounts = mockService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }


}
