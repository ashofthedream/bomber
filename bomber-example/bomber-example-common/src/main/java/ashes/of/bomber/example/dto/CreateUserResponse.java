package ashes.of.bomber.example.dto;

import ashes.of.bomber.example.models.User;

public class CreateUserResponse {
    private User user;

    public User getUser() {
        return user;
    }

    public CreateUserResponse setUser(User user) {
        this.user = user;
        return this;
    }
}
