package WorkflowManager.issue.model;

import WorkflowManager.issue.IssueType;

public record CreateIssueInput(
        String title,
        String description,
        Short storyPoints,
        String parentId,
        String projectId,
        IssueType issueType,
        String status

) { }
