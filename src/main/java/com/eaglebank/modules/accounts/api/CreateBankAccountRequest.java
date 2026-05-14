package com.eaglebank.modules.accounts.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateBankAccountRequest(
    @NotBlank String name,
    @NotBlank @Pattern(regexp = "^personal$") String accountType
) {
}

