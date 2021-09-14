package ashes.of.bomber.example.accounts.services;

import ashes.of.bomber.example.models.Account;
import ashes.of.bomber.example.models.User;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final AtomicLong idSeq = new AtomicLong();
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();


    public Account createAccount(User user) {
        var account = new Account()
                .setId(idSeq.incrementAndGet())
                .setUserId(user.getId())
                .setAmount(BigDecimal.valueOf(1000));

        accounts.put(account.getId(), account);
        return account;
    }

    @Nullable
    public Account getAccount(long accountId) {
        return accounts.get(accountId);
    }

    @Nullable
    public List<Account> getAccountByUser(long userId) {
        return accounts.values()
                .stream()
                .filter(account -> account.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }
}
