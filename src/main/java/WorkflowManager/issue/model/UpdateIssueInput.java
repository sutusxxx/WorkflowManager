package WorkflowManager.issue.model;

import java.time.LocalDateTime;

public record UpdateIssueInput(
        String title,
        String description,
        Short storyPoints,
        LocalDateTime dueDate
) { }
