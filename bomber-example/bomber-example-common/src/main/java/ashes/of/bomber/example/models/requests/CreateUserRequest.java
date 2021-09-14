package ashes.of.bomber.example.models.requests;

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
