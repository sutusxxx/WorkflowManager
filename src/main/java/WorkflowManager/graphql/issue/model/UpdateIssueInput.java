package WorkflowManager.graphql.issue.model;

import java.time.LocalDateTime;

public record UpdateIssueInput(
        String title,
        String description,
        Short storyPoints,
        LocalDateTime dueDate
) { }
