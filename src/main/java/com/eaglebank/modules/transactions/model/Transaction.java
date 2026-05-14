package com.eaglebank.modules.transactions.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Transaction(
    String id,
    String accountNumber,
    String userId,
    BigDecimal amount,
    String currency,
    String type,
    String reference,
    OffsetDateTime createdTimestamp
) {
}

