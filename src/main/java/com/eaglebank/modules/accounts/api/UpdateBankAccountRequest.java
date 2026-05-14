package com.eaglebank.modules.accounts.api;

import jakarta.validation.constraints.Pattern;

public record UpdateBankAccountRequest(
    String name,
    @Pattern(regexp = "^personal$") String accountType
) {
}

