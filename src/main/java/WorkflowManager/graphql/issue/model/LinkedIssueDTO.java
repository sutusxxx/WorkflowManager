package WorkflowManager.graphql.issue.model;

import WorkflowManager.graphql.issue.IssueLinkType;

public class LinkedIssueDTO {
    private IssueLinkType linkType;
    private String targetIssueId;

    public IssueLinkType getLinkType() {
        return linkType;
    }

    public String getTargetIssueId() {
        return targetIssueId;
    }
}
