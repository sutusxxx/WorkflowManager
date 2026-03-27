package WorkflowManager.permission;

import WorkflowManager.permission.model.AssignPermissionRequest;
import WorkflowManager.permission.model.CreatePermissionRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/assign")
    public void assign(@RequestBody AssignPermissionRequest request) {
        permissionService.assign(request);
    }

    @PostMapping("/create")
    public void create(@RequestBody CreatePermissionRequest request) {
        permissionService.create(request);
    }
}
