package WorkflowManager.project.model;

public record UpdateProjectInput(
        String name,
        String description,
        Boolean isPrivate
) { }
