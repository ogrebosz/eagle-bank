package com.eaglebank.modules.accounts.service;

import com.eaglebank.common.error.ApiException;
import com.eaglebank.modules.accounts.api.BankAccountResponse;
import com.eaglebank.modules.accounts.api.CreateBankAccountRequest;
import com.eaglebank.modules.accounts.api.ListBankAccountsResponse;
import com.eaglebank.modules.accounts.api.UpdateBankAccountRequest;
import com.eaglebank.modules.accounts.model.Account;
import com.eaglebank.modules.accounts.repository.AccountRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private static final String SORT_CODE = "10-10-10";
    private static final AtomicInteger ACCOUNT_COUNTER = new AtomicInteger(100000);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public BankAccountResponse create(String userId, CreateBankAccountRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        Account account = new Account(
            generateAccountNumber(),
            SORT_CODE,
            request.name(),
            request.accountType(),
            BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY),
            "GBP",
            userId,
            now,
            now
        );
        return toResponse(accountRepository.save(account));
    }

    public ListBankAccountsResponse list(String userId) {
        List<BankAccountResponse> accounts = accountRepository.findByUserId(userId).stream().map(this::toResponse).toList();
        return new ListBankAccountsResponse(accounts);
    }

    public BankAccountResponse fetchOwned(String userId, String accountNumber) {
        return toResponse(getOwnedAccount(userId, accountNumber));
    }

    public BankAccountResponse updateOwned(String userId, String accountNumber, UpdateBankAccountRequest request) {
        Account existing = getOwnedAccount(userId, accountNumber);
        Account updated = new Account(
            existing.accountNumber(),
            existing.sortCode(),
            request.name() != null ? request.name() : existing.name(),
            request.accountType() != null ? request.accountType() : existing.accountType(),
            existing.balance(),
            existing.currency(),
            existing.userId(),
            existing.createdTimestamp(),
            OffsetDateTime.now()
        );

        return toResponse(accountRepository.save(updated));
    }

    public void deleteOwned(String userId, String accountNumber) {
        Account existing = getOwnedAccount(userId, accountNumber);
        accountRepository.deleteByAccountNumber(existing.accountNumber());
    }

    public Account getOwnedAccount(String userId, String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Bank account was not found"));

        if (!account.userId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "The user is not allowed to access the bank account details");
        }

        return account;
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    private BankAccountResponse toResponse(Account account) {
        return new BankAccountResponse(
            account.accountNumber(),
            account.sortCode(),
            account.name(),
            account.accountType(),
            account.balance(),
            account.currency(),
            account.createdTimestamp(),
            account.updatedTimestamp()
        );
    }

    private String generateAccountNumber() {
        int number = ACCOUNT_COUNTER.getAndIncrement();
        return "01" + String.format("%06d", number % 1_000_000);
    }
}

