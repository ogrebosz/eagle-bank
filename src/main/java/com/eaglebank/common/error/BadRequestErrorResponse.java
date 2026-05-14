package com.eaglebank.common.error;

import java.util.List;

public record BadRequestErrorResponse(String message, List<ValidationErrorDetail> details) {
}

