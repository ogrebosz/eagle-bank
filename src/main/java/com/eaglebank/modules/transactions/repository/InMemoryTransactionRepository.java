package com.eaglebank.modules.transactions.repository;

import com.eaglebank.modules.transactions.model.Transaction;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, Transaction> transactionsById = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        transactionsById.put(transaction.id(), transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findByAccountNumber(String accountNumber) {
        return transactionsById.values().stream()
            .filter(t -> t.accountNumber().equals(accountNumber))
            .sorted((a, b) -> a.createdTimestamp().compareTo(b.createdTimestamp()))
            .toList();
    }

    @Override
    public Optional<Transaction> findByIdAndAccountNumber(String transactionId, String accountNumber) {
        Transaction transaction = transactionsById.get(transactionId);
        if (transaction == null || !transaction.accountNumber().equals(accountNumber)) {
            return Optional.empty();
        }

        return Optional.of(transaction);
    }
}

