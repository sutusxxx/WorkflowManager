package com.sutusxxx.graphql.exceptions;

import lombok.Getter;
import org.springframework.graphql.execution.ErrorType;

@Getter
public class AppException extends RuntimeException {
    private final ErrorType errorType;

    public AppException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

}
