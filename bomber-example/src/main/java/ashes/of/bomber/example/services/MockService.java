package ashes.of.bomber.example.services;

import ashes.of.bomber.example.model.Account;
import ashes.of.bomber.example.model.User;
import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MockService {
    private final NameGenerator nameGenerator = new NameGenerator();
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    public User getOrCreateUserById(Long id) {
        return users.computeIfAbsent(id, k -> {
            Name name = nameGenerator.generateName();
            return new User(k, name.getFirstName() + " " + name.getLastName());
        });
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public Account getOrCreateAccountById(Long id) {
        return accounts.computeIfAbsent(id, k -> {
            return new Account(k, BigDecimal.ZERO);
        });
    }

    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }
}
