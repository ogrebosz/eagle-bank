package com.eaglebank.modules.transactions.repository;

import com.eaglebank.modules.transactions.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findByAccountNumber(String accountNumber);

    Optional<Transaction> findByIdAndAccountNumber(String transactionId, String accountNumber);
}

