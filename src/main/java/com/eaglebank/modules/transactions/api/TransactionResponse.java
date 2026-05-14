package com.eaglebank.modules.transactions.api;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponse(
    String id,
    BigDecimal amount,
    String currency,
    String type,
    String reference,
    String userId,
    OffsetDateTime createdTimestamp
) {
}

