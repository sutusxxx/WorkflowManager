package com.sutusxxx.graphql.common.exceptions;

import org.springframework.http.HttpStatus;

public class ProjectNotFoundException extends AppException {
    public ProjectNotFoundException(String id) {
        super("Project not found: " + id, HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND");
    }
}
