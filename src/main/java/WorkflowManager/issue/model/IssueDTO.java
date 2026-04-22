package WorkflowManager.issue.model;

import WorkflowManager.issue.IssueType;
import WorkflowManager.project.model.ProjectDTO;
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
    private String parentId;
    private List<IssueDTO> children = new ArrayList<>();
}
