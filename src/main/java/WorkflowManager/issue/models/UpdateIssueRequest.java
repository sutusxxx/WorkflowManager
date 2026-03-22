package WorkflowManager.issue.models;

import java.time.LocalDateTime;

public class UpdateIssueRequest {
    private String title;
    private String description;
    private Short storyPoints;
    private LocalDateTime dueDate;
    private String status;

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
}
