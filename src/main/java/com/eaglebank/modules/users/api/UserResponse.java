package com.eaglebank.modules.users.api;

import java.time.OffsetDateTime;

public record UserResponse(
    String id,
    String name,
    UserAddressDto address,
    String phoneNumber,
    String email,
    OffsetDateTime createdTimestamp,
    OffsetDateTime updatedTimestamp
) {
}

