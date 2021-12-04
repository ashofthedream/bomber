package ashes.of.bomber.example.users.services;

import ashes.of.bomber.example.accounts.client.AccountClient;
import ashes.of.bomber.example.dto.CreateAccountsRequest;
import ashes.of.bomber.example.dto.CreateAccountsResponse;
import ashes.of.bomber.example.dto.GetAccountsResponse;
import ashes.of.bomber.example.models.User;
import org.ajbrown.namemachine.NameGenerator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final NameGenerator nameGenerator = new NameGenerator();

    private final AtomicLong userIdSeq = new AtomicLong();
    private final Map<Long, User> usersById = new ConcurrentHashMap<>();

    private final AccountClient accountClient;


    public UserService(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    public Mono<User> createUser(String username) {
        var user = new User()
                .setId(userIdSeq.incrementAndGet())
                .setUsername(username);

        usersById.put(user.getId(), user);

        return accountClient.createAccounts(new CreateAccountsRequest().setUser(user))
                .map(CreateAccountsResponse::getAccounts)
                .map(accounts -> {
                    user.setAccounts(accounts);
                    return user;
                });
    }

    public Mono<User> getUser(long userId) {
        var user = usersById.get(userId);
        if (user == null)
            return Mono.error(new RuntimeException("User with id: " + userId + " not found"));

        return Mono.just(user);
    }

    public Mono<User> getUserWithAccounts(long userId) {
        return getUser(userId)
                .flatMap(user -> {
                    return accountClient.getAccountsByUser(userId)
                            .map(GetAccountsResponse::getAccounts)
                            .map(accounts -> {
                                user.setAccounts(accounts);

                                return user;
                            });
                });
    }

    public Flux<User> getAllUsers() {
        return Flux.fromIterable(usersById.values());
    }
}
