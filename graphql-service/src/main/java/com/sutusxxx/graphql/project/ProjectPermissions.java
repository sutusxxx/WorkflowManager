package com.sutusxxx.graphql.project;

import java.util.Map;
import java.util.Set;

public final class ProjectPermissions {

    private static final Map<ProjectRole, Set<ProjectPermission>> ROLE_PERMISSIONS = Map.of(
            ProjectRole.VIEWER, Set.of(
                    ProjectPermission.VIEW_PROJECT,
                    ProjectPermission.VIEW_ISSUES,
                    ProjectPermission.VIEW_SPRINT
            ),
            ProjectRole.MEMBER, Set.of(
                    ProjectPermission.VIEW_PROJECT,
                    ProjectPermission.VIEW_ISSUES,
                    ProjectPermission.VIEW_SPRINT,
                    ProjectPermission.CREATE_ISSUE,
                    ProjectPermission.EDIT_ISSUE,
                    ProjectPermission.DELETE_ISSUE,
                    ProjectPermission.TRANSITION_ISSUE,
                    ProjectPermission.CREATE_SPRINT,
                    ProjectPermission.START_SPRINT,
                    ProjectPermission.COMPLETE_SPRINT,
                    ProjectPermission.DELETE_SPRINT,
                    ProjectPermission.MANAGE_STATUSES
            ),
            ProjectRole.ADMIN, Set.of(
                    // all of MEMBER's permissions, plus:
                    ProjectPermission.VIEW_PROJECT,
                    ProjectPermission.VIEW_ISSUES,
                    ProjectPermission.VIEW_SPRINT,
                    ProjectPermission.CREATE_ISSUE,
                    ProjectPermission.EDIT_ISSUE,
                    ProjectPermission.DELETE_ISSUE,
                    ProjectPermission.TRANSITION_ISSUE,
                    ProjectPermission.CREATE_SPRINT,
                    ProjectPermission.START_SPRINT,
                    ProjectPermission.COMPLETE_SPRINT,
                    ProjectPermission.DELETE_SPRINT,
                    ProjectPermission.MANAGE_STATUSES,
                    ProjectPermission.MANAGE_MEMBERS,
                    ProjectPermission.EDIT_PROJECT,
                    ProjectPermission.DELETE_PROJECT
            )
    );

    public static boolean hasPermission(ProjectRole role, ProjectPermission permission) {
        return ROLE_PERMISSIONS.getOrDefault(role, Set.of()).contains(permission);
    }

    private ProjectPermissions() {}
}