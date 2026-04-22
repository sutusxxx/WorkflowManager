package WorkflowManager.issue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueLink {
    private String targetIssueId;
    private IssueLinkType linkType;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String createdBy;
}
