package com.eaglebank.common.error;

public record ValidationErrorDetail(String field, String message, String type) {
}

