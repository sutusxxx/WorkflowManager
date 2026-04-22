package WorkflowManager.project.model;

public record CreateProjectInput(
        String name,
        String key,
        String description,
        Boolean isPrivate
) { }
