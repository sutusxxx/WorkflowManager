package com.sutusxxx.graphql.project.resolver;

import com.sutusxxx.graphql.project.Project;
import com.sutusxxx.graphql.project.ProjectService;
import com.sutusxxx.user.UserService;
import com.sutusxxx.user.model.UserSummaryDTO;
import graphql.relay.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Controller
public class ProjectQueryResolver {
    private final ProjectService projectService;
    private final UserService userService;

    private final Executor batchExecutor;

    @Autowired
    public ProjectQueryResolver(
            ProjectService projectService,
            UserService userService,
            Executor batchExecutor) {
        this.projectService = projectService;
        this.userService = userService;
        this.batchExecutor = batchExecutor;
    }

    @QueryMapping
    public Connection<Project> projects(@Argument Integer first, @Argument String after) {
        return projectService.getProjects(first, after);
    }

    @QueryMapping List<Project> recentProjects(@AuthenticationPrincipal Jwt jwt, @Argument Integer limit) {
        return projectService.getRecentProjects(jwt.getSubject(), limit);
    }

    @QueryMapping
    public Project project(@AuthenticationPrincipal Jwt jwt, @Argument String id) {
        projectService.trackView(jwt.getSubject(), id);
        return projectService.getProjectById(id);
    }

    @BatchMapping(typeName = "Project", field = "createdBy")
    public CompletableFuture<Map<Project, UserSummaryDTO>> createdBy(List<Project> projects) {
        return CompletableFuture.supplyAsync(
                () -> userService.batchLoadUsers(projects, Project::getCreatedBy),
                batchExecutor
        );
    }

    @BatchMapping(typeName = "Project", field = "modifiedBy")
    public CompletableFuture<Map<Project, UserSummaryDTO>> modifiedBy(List<Project> projects) {
        return CompletableFuture.supplyAsync(
                () -> userService.batchLoadUsers(projects, Project::getModifiedBy),
                batchExecutor
        );
    }
}
