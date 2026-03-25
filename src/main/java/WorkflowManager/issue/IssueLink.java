package WorkflowManager.issue;

import WorkflowManager.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "issue_links")
public class IssueLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_issue_id", nullable = false)
    private Issue sourceIssue;

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_issue_id", nullable = false)
    private Issue targetIssue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueLinkType linkType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Issue getSourceIssue() {
        return sourceIssue;
    }

    public void setSourceIssue(Issue sourceIssue) {
        this.sourceIssue = sourceIssue;
    }

    public Issue getTargetIssue() {
        return targetIssue;
    }

    public void setTargetIssue(Issue targetIssue) {
        this.targetIssue = targetIssue;
    }

    public IssueLinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(IssueLinkType linkType) {
        this.linkType = linkType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
