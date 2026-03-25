package WorkflowManager.issue.model;

import WorkflowManager.issue.IssueType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class CreateIssueRequest {
    @NotBlank(message = "Title must be provided")
    private String title;
    private String description;

    @Min(value = 1, message = "Story points must be at least 1")
    @Max(value = 13, message = "Story points cannot exceed 13")
    private Short storyPoints;

    private LocalDateTime dueDate;
    private String parentKey;

    @NotBlank(message = "Project key is required")
    private String projectKey;

    @NotNull
    private IssueType type;

    private String status;
    private Long assignedUserId;
    private Long reporterUserId;
    private List<LinkedIssueDTO> linkedIssues;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Short getStoryPoints() {
        return storyPoints;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public String getParentKey() {
        return parentKey;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public IssueType getType() {
        return type;
    }

    public List<LinkedIssueDTO> getLinkedIssues() {
        return linkedIssues;
    }

    public Long getReporterUserId() {
        return reporterUserId;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public String getStatus() {
        return status;
    }
}
