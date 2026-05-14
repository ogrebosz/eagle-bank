package com.eaglebank.modules.users.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    String name,
    @Valid UserAddressDto address,
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$") String phoneNumber,
    @Email String email,
    @Size(min = 8, max = 128) String password
) {
}

