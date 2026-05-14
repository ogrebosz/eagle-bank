package com.eaglebank.modules.transactions.api;

import com.eaglebank.modules.transactions.service.TransactionService;
import com.eaglebank.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrentUser currentUser;

    public TransactionController(TransactionService transactionService, CurrentUser currentUser) {
        this.transactionService = transactionService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
        @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber,
        @Valid @RequestBody CreateTransactionRequest request
    ) {
        return transactionService.create(currentUser.id(), accountNumber, request);
    }

    @GetMapping
    public ListTransactionsResponse listAccountTransaction(
        @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber
    ) {
        return transactionService.listByAccount(currentUser.id(), accountNumber);
    }

    @GetMapping("/{transactionId}")
    public TransactionResponse fetchAccountTransactionByID(
        @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber,
        @PathVariable @Pattern(regexp = "^tan-[A-Za-z0-9]+$") String transactionId
    ) {
        return transactionService.fetchById(currentUser.id(), accountNumber, transactionId);
    }
}

