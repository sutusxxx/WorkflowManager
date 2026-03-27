package WorkflowManager.permission.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AssignPermissionRequest {
    @NotNull(message = "User id must be provided")
    private Long userId;

    @NotEmpty(message = "At least one permission must be passed")
    private List<String> permissions;

    public Long getUserId() {
        return userId;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
