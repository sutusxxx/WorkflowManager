package com.sutusxxx.graphql.common.exceptions;

import org.springframework.http.HttpStatus;

public class IssueNotFoundException extends AppException {
    public IssueNotFoundException(Long id) {
        super("Issue not found: " + id, HttpStatus.NOT_FOUND, "ISSUE_NOT_FOUND");
    }

    public IssueNotFoundException(String key) {
        super("Issue not found: " + key, HttpStatus.NOT_FOUND, "ISSUE_NOT_FOUND");
    }
}
