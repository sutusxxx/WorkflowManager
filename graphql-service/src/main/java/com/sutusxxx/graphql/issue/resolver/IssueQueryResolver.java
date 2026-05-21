package com.sutusxxx.graphql.issue.resolver;

import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.IssueService;
import com.sutusxxx.graphql.issue.Status;
import com.sutusxxx.graphql.issue.model.IssueLinkDTO;
import com.sutusxxx.graphql.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import com.sutusxxx.user.UserService;
import com.sutusxxx.user.model.UserSummaryDTO;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class IssueQueryResolver {
    private final IssueService issueService;
    private final UserService userService;

    @Autowired
    public IssueQueryResolver(IssueService issueService, UserService userService) {
        this.issueService = issueService;
        this.userService = userService;
    }

    @QueryMapping()
    public Issue issueByKey(@Argument String key) {
        log.debug("[ISSUE_QUERY] Getting issue by key '{}'", key);
        return issueService.getIssueByKey(key);
    }

    @BatchMapping(typeName = "Issue", field = "children")
    public Map<Issue, List<Issue>> subIssues(List<Issue> issues) {
        log.debug("[ISSUE_QUERY] Loading sub-issues");
        return issueService.loadChildren(issues);
    }

    @BatchMapping(typeName = "Issue", field = "links")
    public Map<Issue, List<IssueLinkDTO>> links(List<Issue> issues) {
        log.debug("[ISSUE_QUERY] Loading links");
        return issueService.loadLinks(issues);
    }

    @BatchMapping(typeName = "Issue", field = "createdBy")
    public Map<Issue, UserSummaryDTO> createdBy(List<Issue> issues) {
        return userService.batchLoadUsers(issues, Issue::getCreatedBy);
    }

    @BatchMapping(typeName = "Issue", field = "modifiedBy")
    public Map<Issue, UserSummaryDTO> modifiedBy(List<Issue> issues) {
        return userService.batchLoadUsers(issues, Issue::getModifiedBy);
    }

    @BatchMapping(typeName = "Issue", field = "assigned")
    public Map<Issue, UserSummaryDTO> assigned(List<Issue> issues) {
        return userService.batchLoadUsers(issues, Issue::getAssignee);
    }

    @BatchMapping(typeName = "Issue", field = "reporter")
    public Map<Issue, UserSummaryDTO> reporter(List<Issue> issues) {
        return userService.batchLoadUsers(issues, Issue::getReporter);
    }

    @BatchMapping(typeName = "Issue", field = "status")
    public Map<Issue, Status> status(List<Issue> issues) {
        return issueService.batchLoadStatuses(issues);
    }

    @BatchMapping(typeName = "Issue", field = "parent")
    public Map<Issue, Issue> parent(List<Issue> issues) {
        return issueService.batchLoadParents(issues);
    }

    @BatchMapping(typeName = "Issue", field = "project")
    public Map<Issue, Project> project(List<Issue> issues) {
        return issueService.batchLoadProjects(issues);
    }
}
