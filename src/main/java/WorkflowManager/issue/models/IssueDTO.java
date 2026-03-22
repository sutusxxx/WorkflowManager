package WorkflowManager.issue.models;

import WorkflowManager.issue.IssueType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IssueDTO {
    private String title;
    private String key;
    private String description;
    private Short storyPoints;
    private LocalDateTime dueDate;
    private String status;
    private String projectKey;
    private String parentKey;
    private IssueType type;
    private List<IssueSummaryDTO> subIssues = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public List<IssueSummaryDTO> getSubIssues() {
        return subIssues;
    }

    public void setSubIssues(List<IssueSummaryDTO> subIssues) {
        this.subIssues = subIssues;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
