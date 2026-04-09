package WorkflowManager.board.model;

import WorkflowManager.issue.model.IssueSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public class SprintDTO {
    private List<IssueSummaryDTO> issues;
    private LocalDate startDate;
    private LocalDate endDate;
    private String goal;
    private String name;
    private String description;
    private String projectKey;

    public List<IssueSummaryDTO> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueSummaryDTO> issues) {
        this.issues = issues;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
}
