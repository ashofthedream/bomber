package ashes.of.bomber.atc.model;

public class User {
    private String username;

    public User(String username) {
        this.username = username;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }
}
