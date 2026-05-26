package com.sutusxxx.graphql.sprint.resolver;

import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.IssueService;
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

    public SprintQueryResolver(SprintService sprintService, IssueService issueService) {
        this.sprintService = sprintService;
        this.issueService = issueService;
    }

    @QueryMapping
    public Sprint activeSprint(@Argument String projectId) {
        return sprintService.getActiveSprintByProjectId(projectId);
    }

    @SchemaMapping(typeName = "Sprint", field = "issues")
    public List<Issue> issues(Sprint sprint) {
        return issueService.getIssuesBySprintId(sprint.getId());
    }
}
