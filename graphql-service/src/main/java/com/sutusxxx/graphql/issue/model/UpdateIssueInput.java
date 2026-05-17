package com.sutusxxx.graphql.issue.model;

import com.sutusxxx.graphql.issue.Priority;

import java.time.LocalDateTime;

public record UpdateIssueInput(
        String title,
        String description,
        Short storyPoints,
        LocalDateTime dueDate,
        Priority priority
) { }
