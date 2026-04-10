package WorkflowManager.issue.model;

import WorkflowManager.issue.IssueLinkType;

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
