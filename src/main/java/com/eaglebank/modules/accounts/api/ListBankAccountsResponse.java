package com.eaglebank.modules.accounts.api;

import java.util.List;

public record ListBankAccountsResponse(List<BankAccountResponse> accounts) {
}

