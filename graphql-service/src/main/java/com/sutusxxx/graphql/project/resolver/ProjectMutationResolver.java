package com.sutusxxx.graphql.project.resolver;

import com.sutusxxx.graphql.issue.Status;
import com.sutusxxx.graphql.issue.model.CreateStatusInput;
import com.sutusxxx.graphql.project.Project;
import com.sutusxxx.graphql.project.ProjectService;
import com.sutusxxx.graphql.project.model.AddTransitionInput;
import com.sutusxxx.graphql.project.model.CreateProjectInput;
import com.sutusxxx.graphql.project.model.UpdateProjectInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectMutationResolver {
    private final ProjectService projectService;

    @Autowired
    public ProjectMutationResolver(ProjectService projectService) {
        this.projectService = projectService;
    }

    @MutationMapping
    public Project createProject(@Argument CreateProjectInput input) {
        return projectService.createProject(input);
    }

    @MutationMapping
    public Project updateProject(@Argument String id, @Argument UpdateProjectInput input) {
        return projectService.updateProject(id, input);
    }

    @MutationMapping
    public Status createStatus(@Argument String projectId, @Argument CreateStatusInput input) {
        return projectService.addStatus(projectId, input);
    }

    @MutationMapping
    public Project addTransition(@Argument String projectId, @Argument AddTransitionInput input) {
        return projectService.addTransition(projectId, input.fromStatusId(), input.toStatusId());
    }
}
