package com.sutusxxx.graphql.issue.resolver;

import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.IssueService;
import com.sutusxxx.graphql.issue.Status;
import com.sutusxxx.graphql.issue.model.IssueLinkDTO;
import com.sutusxxx.graphql.pagination.Page;
import com.sutusxxx.graphql.project.Project;
import graphql.relay.Connection;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Controller
public class IssueQueryResolver {
    private final IssueService issueService;
    private final UserService userService;

    private final Executor batchExecutor;

    @Autowired
    public IssueQueryResolver(IssueService issueService, UserService userService, Executor batchExecutor) {
        this.issueService = issueService;
        this.userService = userService;
        this.batchExecutor = batchExecutor;
    }

    @QueryMapping
    public Issue issueByKey(@Argument String key) {
        log.debug("[ISSUE_QUERY] Getting issue by key '{}'", key);
        return issueService.getIssueByKey(key);
    }

    @QueryMapping
    public Page<Issue> issues(@Argument String projectId, @Argument Integer page, @Argument Integer pageSize) {
        return issueService.getIssuesByProjectId(projectId, page, pageSize);
    }

    @QueryMapping
    public Connection<Issue> backlog(@Argument String projectId, @Argument Integer first, @Argument String after) {
        return issueService.getBacklogIssuesByProjectId(projectId, first, after);
    }

    @BatchMapping(typeName = "Issue", field = "children")
    public CompletableFuture<Map<Issue, List<Issue>>> subIssues(List<Issue> issues) {
        log.debug("[ISSUE_QUERY] Loading sub-issues");
        return CompletableFuture.supplyAsync(
                () -> issueService.loadChildren(issues),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Issue", field = "links")
    public CompletableFuture<Map<Issue, List<IssueLinkDTO>>> links(List<Issue> issues) {
        log.debug("[ISSUE_QUERY] Loading links");
        return CompletableFuture.supplyAsync(
                () -> issueService.loadLinks(issues),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Issue", field = "createdBy")
    public CompletableFuture<Map<Issue, UserSummaryDTO>> createdBy(List<Issue> issues) {
        return CompletableFuture.supplyAsync(
                () -> userService.batchLoadUsers(issues, Issue::getCreatedBy),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Issue", field = "modifiedBy")
    public CompletableFuture<Map<Issue, UserSummaryDTO>> modifiedBy(List<Issue> issues) {
        return CompletableFuture.supplyAsync(
                () -> userService.batchLoadUsers(issues, Issue::getModifiedBy),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Issue", field = "assigned")
    public CompletableFuture<Map<Issue, UserSummaryDTO>> assigned(List<Issue> issues) {
        return CompletableFuture.supplyAsync(
                () -> userService.batchLoadUsers(issues, Issue::getAssignee),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Issue", field = "reporter")
    public CompletableFuture<Map<Issue, UserSummaryDTO>> reporter(List<Issue> issues) {
        return CompletableFuture.supplyAsync(
                () -> userService.batchLoadUsers(issues, Issue::getReporter),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Issue", field = "status")
    public CompletableFuture<Map<Issue, Status>> status(List<Issue> issues) {
        return CompletableFuture.supplyAsync(
                () -> issueService.batchLoadStatuses(issues),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Issue", field = "parent")
    public CompletableFuture<Map<Issue, Issue>> parent(List<Issue> issues) {
        return CompletableFuture.supplyAsync(
                () -> issueService.batchLoadParents(issues),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Issue", field = "project")
    public CompletableFuture<Map<Issue, Project>> project(List<Issue> issues) {
        return CompletableFuture.supplyAsync(
                () -> issueService.batchLoadProjects(issues),
                batchExecutor
        );
    }
}
