package com.eaglebank.modules.auth.api;

public record LoginResponse(String accessToken, String tokenType, long expiresInSeconds) {
}

