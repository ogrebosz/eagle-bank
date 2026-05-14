package com.eaglebank.modules.users.model;

public record UserAddress(
    String line1,
    String line2,
    String line3,
    String town,
    String county,
    String postcode
) {
}

