package com.sutusxxx.graphql.project;

public enum ProjectPermission {
    // Read
    VIEW_PROJECT,
    VIEW_ISSUES,
    VIEW_SPRINT,

    // Issues
    CREATE_ISSUE,
    EDIT_ISSUE,
    DELETE_ISSUE,
    TRANSITION_ISSUE,

    // Sprints
    CREATE_SPRINT,
    START_SPRINT,
    COMPLETE_SPRINT,
    DELETE_SPRINT,

    // Project management
    MANAGE_MEMBERS,
    MANAGE_STATUSES,
    EDIT_PROJECT,
    DELETE_PROJECT
}
