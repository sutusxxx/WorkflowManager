package WorkflowManager.permission.model;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AssignPermissionRequest {
    @NotNull(message = "User id must be provided")
    private Long userId;

    private List<String> permissions;

    public Long getUserId() {
        return userId;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
