package ashes.of.bomber.example.models;

import java.util.List;


public class User {
    private Long id;
    private String username;
    private List<Account> accounts;

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public User setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        return this;
    }
}
