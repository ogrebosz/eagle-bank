package com.eaglebank.modules.accounts.api;

import com.eaglebank.modules.accounts.service.AccountService;
import com.eaglebank.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Validated
public class AccountController {

    private final AccountService accountService;
    private final CurrentUser currentUser;

    public AccountController(AccountService accountService, CurrentUser currentUser) {
        this.accountService = accountService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountResponse createAccount(@Valid @RequestBody CreateBankAccountRequest request) {
        return accountService.create(currentUser.id(), request);
    }

    @GetMapping
    public ListBankAccountsResponse listAccounts() {
        return accountService.list(currentUser.id());
    }

    @GetMapping("/{accountNumber}")
    public BankAccountResponse fetchAccountByAccountNumber(
        @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber
    ) {
        return accountService.fetchOwned(currentUser.id(), accountNumber);
    }

    @PatchMapping("/{accountNumber}")
    public BankAccountResponse updateAccountByAccountNumber(
        @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber,
        @Valid @RequestBody UpdateBankAccountRequest request
    ) {
        return accountService.updateOwned(currentUser.id(), accountNumber, request);
    }

    @DeleteMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccountByAccountNumber(
        @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber
    ) {
        accountService.deleteOwned(currentUser.id(), accountNumber);
    }
}

