package WorkflowManager.project.model;

import WorkflowManager.issue.model.IssueDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectDTO {
    private String id;
    private String name;
    private String key;
    private String description;
    private List<IssueDTO> issues = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
