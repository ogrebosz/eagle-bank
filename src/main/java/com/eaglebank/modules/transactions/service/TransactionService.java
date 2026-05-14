package com.eaglebank.modules.transactions.service;

import com.eaglebank.common.error.ApiException;
import com.eaglebank.modules.accounts.model.Account;
import com.eaglebank.modules.accounts.service.AccountService;
import com.eaglebank.modules.transactions.api.CreateTransactionRequest;
import com.eaglebank.modules.transactions.api.ListTransactionsResponse;
import com.eaglebank.modules.transactions.api.TransactionResponse;
import com.eaglebank.modules.transactions.model.Transaction;
import com.eaglebank.modules.transactions.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public TransactionResponse create(String userId, String accountNumber, CreateTransactionRequest request) {
        Account account = accountService.getOwnedAccount(userId, accountNumber);
        BigDecimal amount = request.amount().setScale(2, RoundingMode.HALF_UP);

        BigDecimal newBalance;
        if ("deposit".equals(request.type())) {
            newBalance = account.balance().add(amount);
        } else {
            if (account.balance().compareTo(amount) < 0) {
                throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient funds to process transaction");
            }
            newBalance = account.balance().subtract(amount);
        }

        Account updatedAccount = new Account(
            account.accountNumber(),
            account.sortCode(),
            account.name(),
            account.accountType(),
            newBalance,
            account.currency(),
            account.userId(),
            account.createdTimestamp(),
            OffsetDateTime.now()
        );
        accountService.save(updatedAccount);

        Transaction transaction = new Transaction(
            generateTransactionId(),
            account.accountNumber(),
            userId,
            amount,
            request.currency(),
            request.type(),
            request.reference(),
            OffsetDateTime.now()
        );

        return toResponse(transactionRepository.save(transaction));
    }

    public ListTransactionsResponse listByAccount(String userId, String accountNumber) {
        accountService.getOwnedAccount(userId, accountNumber);
        return new ListTransactionsResponse(transactionRepository.findByAccountNumber(accountNumber).stream().map(this::toResponse).toList());
    }

    public TransactionResponse fetchById(String userId, String accountNumber, String transactionId) {
        accountService.getOwnedAccount(userId, accountNumber);
        Transaction transaction = transactionRepository.findByIdAndAccountNumber(transactionId, accountNumber)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transaction was not found"));

        return toResponse(transaction);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.id(),
            transaction.amount(),
            transaction.currency(),
            transaction.type(),
            transaction.reference(),
            transaction.userId(),
            transaction.createdTimestamp()
        );
    }

    private String generateTransactionId() {
        return "tan-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}

