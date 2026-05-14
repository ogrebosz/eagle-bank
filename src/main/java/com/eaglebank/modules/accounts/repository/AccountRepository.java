package com.eaglebank.modules.accounts.repository;

import com.eaglebank.modules.accounts.model.Account;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserId(String userId);

    void deleteByAccountNumber(String accountNumber);
}

