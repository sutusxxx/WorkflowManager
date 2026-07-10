package com.sutusxxx.graphql.project.resolver;

import com.sutusxxx.graphql.annotation.CurrentUser;
import com.sutusxxx.graphql.issue.Status;
import com.sutusxxx.graphql.issue.model.CreateStatusInput;
import com.sutusxxx.graphql.project.Project;
import com.sutusxxx.graphql.project.ProjectService;
import com.sutusxxx.graphql.project.model.*;
import com.sutusxxx.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectMutationResolver {
    private final ProjectService projectService;

    @Autowired
    public ProjectMutationResolver(ProjectService projectService) {
        this.projectService = projectService;
    }

    @MutationMapping
    public Project createProject(@Argument CreateProjectInput input, @CurrentUser User currentUser) {
        return projectService.createProject(currentUser, input);
    }

    @MutationMapping
    public Project updateProject(@Argument String id, @Argument UpdateProjectInput input, @CurrentUser User currentUser) {
        return projectService.updateProject(currentUser, id, input);
    }

    @MutationMapping
    public Status createStatus(@Argument String projectId, @Argument CreateStatusInput input, @CurrentUser User currentUser) {
        return projectService.addStatus(currentUser, projectId, input);
    }

    @MutationMapping
    public Project addTransition(@Argument String projectId, @Argument AddTransitionInput input) {
        return projectService.addTransition(projectId, input.fromStatusId(), input.toStatusId());
    }

    @MutationMapping
    public Boolean viewProject(@Argument String projectId, @CurrentUser User currentUser) {
        return projectService.trackView(currentUser, projectId);
    }

    @MutationMapping
    public Project addMember(@Argument AddMemberInput input, @CurrentUser User currentUser) {
        return projectService.assignProjectMember(currentUser, input);
    }

    @MutationMapping
    public Project removeMember(@Argument RemoveMemberInput input, @CurrentUser User currentUser) {
        return projectService.removeProjectMember(currentUser, input);
    }
}
