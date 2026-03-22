package WorkflowManager.project.models;

import WorkflowManager.issue.models.IssueSummaryDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectDTO {
    private String name;
    private String key;
    private String description;
    private List<IssueSummaryDTO> issueSummaries = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<IssueSummaryDTO> getIssueSummaries() {
        return issueSummaries;
    }

    public void setIssueSummaries(List<IssueSummaryDTO> issueSummaries) {
        this.issueSummaries = issueSummaries;
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
