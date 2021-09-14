package ashes.of.bomber.example.models.requests;

import ashes.of.bomber.example.models.User;

public class CreateAccountsRequest {
    private User user;

    public User getUser() {
        return user;
    }

    public CreateAccountsRequest setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        return "CreateAccountsRequest{" +
                "user=" + user +
                '}';
    }
}
