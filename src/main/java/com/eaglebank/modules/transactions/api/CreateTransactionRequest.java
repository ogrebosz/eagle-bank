package com.eaglebank.modules.transactions.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record CreateTransactionRequest(
    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 8, fraction = 2) BigDecimal amount,
    @NotBlank @Pattern(regexp = "^GBP$") String currency,
    @NotBlank @Pattern(regexp = "^(deposit|withdrawal)$") String type,
    String reference
) {
}

