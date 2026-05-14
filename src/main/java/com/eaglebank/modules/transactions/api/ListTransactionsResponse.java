package com.eaglebank.modules.transactions.api;

import java.util.List;

public record ListTransactionsResponse(List<TransactionResponse> transactions) {
}

