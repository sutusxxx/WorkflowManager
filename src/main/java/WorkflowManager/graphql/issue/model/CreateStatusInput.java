package WorkflowManager.graphql.issue.model;

import WorkflowManager.graphql.issue.StatusCategory;

public record CreateStatusInput (
        String name,
        StatusCategory category,
        String color,
        Integer displayOrder,
        Boolean isDefault
) { }
