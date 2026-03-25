package WorkflowManager.issue.model;

import java.time.LocalDateTime;

public class UpdateIssueRequest {
    private String title;
    private String description;
    private Short storyPoints;
    private LocalDateTime dueDate;
    private String status;
    private Long assignedUserId;
    private Long reporterUserId;

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

    public String getStatus() {
        return status;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public Long getReporterUserId() {
        return reporterUserId;
    }
}
