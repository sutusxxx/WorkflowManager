package WorkflowManager.auth.model;

public record RegisterRequest(
    String username,
    String email,
    String password
) {}
