package WorkflowManager.issue.model;

import WorkflowManager.issue.StatusCategory;

public record CreateStatusInput (
        String name,
        StatusCategory category,
        String color,
        Integer displayOrder,
        Boolean isDefault
) { }
