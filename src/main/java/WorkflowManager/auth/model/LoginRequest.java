package WorkflowManager.auth.model;

public record LoginRequest(
    String username,
    String password
) {}
