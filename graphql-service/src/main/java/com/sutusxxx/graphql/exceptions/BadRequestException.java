package com.sutusxxx.graphql.exceptions;

import org.springframework.graphql.execution.ErrorType;

public class BadRequestException extends AppException {
    public BadRequestException(String message, Object... args) {
        super(args.length > 0 ? String.format(message, args) : message, ErrorType.BAD_REQUEST);
    }
}
