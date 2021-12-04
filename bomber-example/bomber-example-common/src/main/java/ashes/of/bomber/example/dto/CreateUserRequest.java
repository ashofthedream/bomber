package ashes.of.bomber.example.dto;

public class CreateUserRequest {
    private String username;

    public String getUsername() {
        return username;
    }

    public CreateUserRequest setUsername(String username) {
        this.username = username;
        return this;
    }
}
