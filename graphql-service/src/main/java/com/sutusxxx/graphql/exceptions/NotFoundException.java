package com.sutusxxx.graphql.exceptions;

import org.springframework.graphql.execution.ErrorType;

public class NotFoundException extends AppException {
    public NotFoundException() {
        super("Not found", ErrorType.NOT_FOUND);
    }
}
