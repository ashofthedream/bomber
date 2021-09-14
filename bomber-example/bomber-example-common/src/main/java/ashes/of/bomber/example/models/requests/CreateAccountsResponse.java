package ashes.of.bomber.example.models.requests;

import ashes.of.bomber.example.models.Account;

import java.util.List;

public class CreateAccountsResponse {
    private List<Account> accounts;

    public List<Account> getAccounts() {
        return accounts;
    }

    public CreateAccountsResponse setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        return this;
    }
}
