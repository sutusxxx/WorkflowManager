package WorkflowManager.sprint;

import WorkflowManager.issue.Issue;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "sprints")
public class Sprint {
    @Id
    private String id;

    private String name;
    private String goal;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Issue> issues;
    private Boolean active;
}
