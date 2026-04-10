package WorkflowManager.issue.model;

import WorkflowManager.issue.IssueType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class CreateIssueInput {
    @NotBlank(message = "Title must be provided")
    private String title;
    private String description;

    @Min(value = 1, message = "Story points must be at least 1")
    @Max(value = 13, message = "Story points cannot exceed 13")
    private Short storyPoints;

    private LocalDateTime dueDate;
    private Long parentId;

    @NotBlank(message = "Project id is required")
    private Long projectId;

    @NotNull
    private IssueType type;

    private String status;
    private Long assignedUserId;
    private Long reporterUserId;
    private List<LinkedIssueDTO> linkedIssues;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Short storyPoints) {
        this.storyPoints = storyPoints;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Long getReporterUserId() {
        return reporterUserId;
    }

    public void setReporterUserId(Long reporterUserId) {
        this.reporterUserId = reporterUserId;
    }

    public List<LinkedIssueDTO> getLinkedIssues() {
        return linkedIssues;
    }

    public void setLinkedIssues(List<LinkedIssueDTO> linkedIssues) {
        this.linkedIssues = linkedIssues;
    }
}
