package WorkflowManager.graphql.issue.model;

import WorkflowManager.graphql.issue.IssueType;
import WorkflowManager.graphql.project.model.ProjectDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class IssueDTO {
    private String id;
    private String title;
    private String key;
    private String description;
    private StatusDTO status;
    private IssueType type;
    private Short storyPoints;
    private LocalDateTime dueDate;
    private ProjectDTO project;
    private List<IssueDTO> children = new ArrayList<>();
}
