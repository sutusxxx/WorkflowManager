package com.sutusxxx.graphql.issue.model;

import com.sutusxxx.graphql.issue.IssueType;

public record CreateIssueInput(
        String title,
        String description,
        Short storyPoints,
        String parentId,
        String projectId,
        IssueType type,
        String status,
        String sprintId
) { }
