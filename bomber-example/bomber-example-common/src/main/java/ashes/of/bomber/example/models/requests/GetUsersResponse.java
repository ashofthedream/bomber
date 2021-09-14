package ashes.of.bomber.example.models.requests;

import ashes.of.bomber.example.models.User;

import java.util.List;

public class GetUsersResponse {
    List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public GetUsersResponse setUsers(List<User> users) {
        this.users = users;
        return this;
    }
}
