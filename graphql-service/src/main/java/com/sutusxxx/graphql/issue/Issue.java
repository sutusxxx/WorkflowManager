package com.sutusxxx.graphql.issue;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "issues")
public class Issue {
    @Id
    private String id;

    @Indexed(unique = true)
    @NotNull(message = "key is required")
    private String key;

    @NotNull(message = "title is required")
    private String title;

    private String description;
    private Short storyPoints;
    private LocalDateTime dueDate;
    private String statusId;

    @NotEmpty
    private String projectId;

    @NotNull
    private IssueType type;

    @Indexed
    private String parentId;

    private List<IssueLink> links = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String modifiedBy;

    private String assignee;
    private String reporter;
    private Priority priority;

    public boolean hasLinkTo(String targetIssueId, IssueLinkType type) {
        return links.stream()
                .anyMatch(link -> link.getTargetIssueId().equals(targetIssueId)
                        && link.getLinkType() == type);
    }

    public void removeLink(String targetIssueId, IssueLinkType type) {
        links.removeIf(l -> l.getTargetIssueId().equals(targetIssueId)
                && l.getLinkType() == type);
    }
}
