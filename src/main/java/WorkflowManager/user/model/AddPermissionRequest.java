package WorkflowManager.user.model;

import java.util.List;

public class AddPermissionRequest {
    private Long userId;
    private List<String> permissions;

    public Long getUserId() {
        return userId;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
