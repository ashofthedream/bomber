package ashes.of.bomber.example.dto;

import ashes.of.bomber.example.models.Account;

import java.util.List;

public class GetAccountsResponse {
    private List<Account> accounts;

    public List<Account> getAccounts() {
        return accounts;
    }

    public GetAccountsResponse setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        return this;
    }
}
