package WorkflowManager.graphql.issue;

import WorkflowManager.auth.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IssueLink {
    private Long id;
    private Issue sourceIssue;
    private Issue targetIssue;
    private IssueLinkType linkType;
    private LocalDateTime createdAt;
    private User createdBy;
}
