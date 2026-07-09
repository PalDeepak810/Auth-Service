package com.auth.dtos;

import org.springframework.http.HttpStatus;

public record EmailErrorResponse(
        String message,
        HttpStatus status
) {
}
