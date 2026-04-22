package WorkflowManager.issue.model;

import WorkflowManager.issue.IssueLinkType;

public record AddIssueLinkInput(
        String sourceIssueId,
        String targetIssueId,
        IssueLinkType linkType,
        String createdBy
) {}
