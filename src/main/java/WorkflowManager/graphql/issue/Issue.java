package WorkflowManager.graphql.issue;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "issues")
public class Issue {
    @Id
    private String id;

    @Indexed(unique = true)
    private String key;

    private String title;
    private String description;
    private Short storyPoints;
    private LocalDateTime dueDate;
    private String statusId;
    private String projectId;
    private IssueType type;
    private String parentID;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private String createdBy;
    private String modifiedBy;
    private String assigned;
    private String reporter;
    private Priority priority;
}
