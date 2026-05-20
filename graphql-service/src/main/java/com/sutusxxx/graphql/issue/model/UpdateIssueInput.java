package com.sutusxxx.graphql.issue.model;

import com.sutusxxx.graphql.issue.Priority;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record UpdateIssueInput(
        String title,
        String description,
        Short storyPoints,
        Instant dueDate,
        Priority priority
) { }
