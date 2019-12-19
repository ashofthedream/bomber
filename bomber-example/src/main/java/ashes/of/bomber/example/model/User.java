package ashes.of.bomber.example.model;

import javax.annotation.Nullable;


public class User {
    @Nullable
    private Long id;

    @Nullable
    private String username;

    public User() {
    }

    public User(@Nullable Long id, @Nullable String username) {
        this.id = id;
        this.username = username;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }
}
