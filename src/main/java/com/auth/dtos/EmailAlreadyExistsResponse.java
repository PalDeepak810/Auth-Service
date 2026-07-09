package com.auth.dtos;

import org.springframework.http.HttpStatus;

public record EmailAlreadyExistsResponse(
        String message,
        HttpStatus status
) {
}
