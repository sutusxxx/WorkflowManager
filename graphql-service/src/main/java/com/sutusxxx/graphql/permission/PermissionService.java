package com.sutusxxx.graphql.permission;

import com.sutusxxx.graphql.project.ProjectPermission;
import com.sutusxxx.graphql.project.ProjectPermissions;
import com.sutusxxx.graphql.project.repository.ProjectRepository;
import com.sutusxxx.user.User;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    private final ProjectRepository projectRepository;

    public PermissionService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public boolean hasPermission(User user, String projectId, ProjectPermission permission) {
        return projectRepository.findById(projectId)
                .map(project -> project.hasMember(user.getId()) &&
                        ProjectPermissions.hasPermission(project.getRoleOf(user.getId()), permission))
                .orElse(false);
    }
}
