package ashes.of.bomber.atc.dto;

public class UserDto {
    private String username;

    public UserDto(String username) {
        this.username = username;
    }

    public UserDto() {
    }

    public String getUsername() {
        return username;
    }

    public UserDto setUsername(String username) {
        this.username = username;
        return this;
    }
}
