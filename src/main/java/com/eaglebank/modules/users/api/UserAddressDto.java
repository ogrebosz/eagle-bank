package com.eaglebank.modules.users.api;

import jakarta.validation.constraints.NotBlank;

public record UserAddressDto(
    @NotBlank String line1,
    String line2,
    String line3,
    @NotBlank String town,
    @NotBlank String county,
    @NotBlank String postcode
) {
}

