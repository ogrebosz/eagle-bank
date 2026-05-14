package com.eaglebank.modules.users.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank String name,
    @Valid UserAddressDto address,
    @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{1,14}$") String phoneNumber,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 128) String password
) {
}

