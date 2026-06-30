package com.sutusxxx.graphql.sprint.resolver;

import com.sutusxxx.graphql.annotation.CurrentUser;
import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.IssueService;
import com.sutusxxx.graphql.pagination.Page;
import com.sutusxxx.graphql.project.Project;
import com.sutusxxx.graphql.project.ProjectService;
import com.sutusxxx.graphql.sprint.Sprint;
import com.sutusxxx.graphql.sprint.SprintService;
import com.sutusxxx.user.User;
import com.sutusxxx.user.UserService;
import com.sutusxxx.user.model.UserSummaryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Controller
public class SprintQueryResolver {
    private final SprintService sprintService;
    private final IssueService issueService;
    private final ProjectService projectService;
    private final UserService userService;

    private final Executor batchExecutor;

    public SprintQueryResolver(
            SprintService sprintService,
            IssueService issueService,
            ProjectService projectService,
            UserService userService,
            Executor batchExecutor
    ) {
        this.sprintService = sprintService;
        this.issueService = issueService;
        this.projectService = projectService;
        this.userService = userService;
        this.batchExecutor = batchExecutor;
    }

    @QueryMapping
    public Sprint sprintBoard(@Argument String projectId) {
        return sprintService.getActiveSprintByProjectId(projectId);
    }

    @QueryMapping
    public Page<Sprint> sprints(@Argument String projectId, @Argument Integer page, @Argument Integer pageSize) {
        return sprintService.getSprintsByProjectId(projectId, page, pageSize);
    }

    @SchemaMapping(typeName = "Sprint", field = "issues")
    public List<Issue> issues(Sprint sprint) {
        return issueService.getIssuesBySprintId(sprint.getId());
    }

    @SchemaMapping(typeName = "Sprint", field = "project")
    public Project project(Sprint sprint) {
        return projectService.getProjectById(sprint.getProjectId());
    }

    @BatchMapping(typeName = "Sprint", field = "createdBy")
    public CompletableFuture<Map<Sprint, UserSummaryDTO>> createdBy(List<Sprint> sprints) {
        return CompletableFuture.supplyAsync(
                () -> userService.batchLoadUsers(sprints, Sprint::getCreatedBy),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Sprint", field = "modifiedBy")
    public CompletableFuture<Map<Sprint, UserSummaryDTO>> modifiedBy(List<Sprint> sprints) {
        return CompletableFuture.supplyAsync(
                () -> userService.batchLoadUsers(sprints, Sprint::getModifiedBy),
                batchExecutor
        );
    }
}
