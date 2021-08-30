package ashes.of.bomber.atc.records;

public class UserRecord {
    private String username;

    public UserRecord(String username) {
        this.username = username;
    }

    public UserRecord() {
    }

    public String getUsername() {
        return username;
    }

    public UserRecord setUsername(String username) {
        this.username = username;
        return this;
    }
}
