package WorkflowManager.graphql.project.model;

public record UpdateProjectInput(
        String name,
        String description,
        Boolean isPrivate
) { }
