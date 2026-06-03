package com.sutusxxx.graphql.project.resolver;

import com.sutusxxx.graphql.project.Project;
import com.sutusxxx.graphql.project.ProjectService;
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
}
