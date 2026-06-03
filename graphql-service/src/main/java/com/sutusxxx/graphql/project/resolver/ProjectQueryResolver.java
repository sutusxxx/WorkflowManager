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
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class ProjectQueryResolver {
    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public ProjectQueryResolver(
            ProjectService projectService,
            UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @QueryMapping
    public Connection<Project> projects(@Argument Integer first, @Argument String after) {
        return projectService.getProjects(first, after);
    }

    @QueryMapping
    public Project project(@Argument String id) {
        return projectService.getProjectById(id);
    }

    @BatchMapping(typeName = "Project", field = "createdBy")
    public Map<Project, UserSummaryDTO> createdBy(List<Project> projects) {
        return userService.batchLoadUsers(projects, Project::getCreatedBy);
    }

    @BatchMapping(typeName = "Project", field = "modifiedBy")
    public Map<Project, UserSummaryDTO> modifiedBy(List<Project> projects) {
        return userService.batchLoadUsers(projects, Project::getModifiedBy);
    }
}
