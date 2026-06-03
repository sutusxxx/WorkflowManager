package com.sutusxxx.graphql.sprint.resolver;

import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.IssueService;
import com.sutusxxx.graphql.project.Project;
import com.sutusxxx.graphql.project.ProjectService;
import com.sutusxxx.graphql.sprint.Sprint;
import com.sutusxxx.graphql.sprint.SprintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
public class SprintQueryResolver {
    private final SprintService sprintService;
    private final IssueService issueService;
    private final ProjectService projectService;

    public SprintQueryResolver(SprintService sprintService, IssueService issueService, ProjectService projectService) {
        this.sprintService = sprintService;
        this.issueService = issueService;
        this.projectService = projectService;
    }

    @QueryMapping
    public Sprint sprintBoard(@Argument String projectId) {
        return sprintService.getActiveSprintByProjectId(projectId);
    }

    @SchemaMapping(typeName = "Sprint", field = "issues")
    public List<Issue> issues(Sprint sprint) {
        return issueService.getIssuesBySprintId(sprint.getId());
    }

    @SchemaMapping(typeName = "Sprint", field = "project")
    public Project project(Sprint sprint) {
        return projectService.getProjectById(sprint.getProjectId());
    }
}
