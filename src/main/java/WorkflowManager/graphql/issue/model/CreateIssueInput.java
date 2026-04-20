package WorkflowManager.graphql.issue.model;

import WorkflowManager.graphql.issue.IssueType;
import WorkflowManager.graphql.issue.model.LinkedIssueDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateIssueInput(
        String title,
        String description,
        Short storyPoints,
        String parentId,
        String projectId,
        IssueType issueType,
        String status

) { }
