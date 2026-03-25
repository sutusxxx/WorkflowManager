package WorkflowManager.issue.model;

import WorkflowManager.issue.IssueLinkType;

public class LinkedIssueDTO {
    private IssueLinkType linkType;
    private String targetIssueKey;

    public IssueLinkType getLinkType() {
        return linkType;
    }

    public String getTargetIssueKey() {
        return targetIssueKey;
    }
}
