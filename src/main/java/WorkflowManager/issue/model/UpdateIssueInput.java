package WorkflowManager.issue.model;

import WorkflowManager.issue.Priority;

import java.time.LocalDateTime;

public record UpdateIssueInput(
        String title,
        String description,
        Short storyPoints,
        LocalDateTime dueDate,
        Priority priority
) { }
