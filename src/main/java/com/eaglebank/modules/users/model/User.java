package com.eaglebank.modules.users.model;

import java.time.OffsetDateTime;

public record User(
    String id,
    String name,
    UserAddress address,
    String phoneNumber,
    String email,
    String passwordHash,
    OffsetDateTime createdTimestamp,
    OffsetDateTime updatedTimestamp
) {
}

