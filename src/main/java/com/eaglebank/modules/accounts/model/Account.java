package com.eaglebank.modules.accounts.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Account(
    String accountNumber,
    String sortCode,
    String name,
    String accountType,
    BigDecimal balance,
    String currency,
    String userId,
    OffsetDateTime createdTimestamp,
    OffsetDateTime updatedTimestamp
) {
}

