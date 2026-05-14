package com.eaglebank.modules.accounts.repository;

import com.eaglebank.modules.accounts.model.Account;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accountsByNumber = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {
        accountsByNumber.put(account.accountNumber(), account);
        return account;
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accountsByNumber.get(accountNumber));
    }

    @Override
    public List<Account> findByUserId(String userId) {
        return accountsByNumber.values().stream().filter(a -> a.userId().equals(userId)).toList();
    }

    @Override
    public void deleteByAccountNumber(String accountNumber) {
        accountsByNumber.remove(accountNumber);
    }
}

