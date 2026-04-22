package WorkflowManager.issue.model;

import WorkflowManager.issue.IssueLinkType;

public record RemoveIssueLinkInput(
        String sourceIssueId,
        String targetIssueId,
        IssueLinkType linkType
) {
}
