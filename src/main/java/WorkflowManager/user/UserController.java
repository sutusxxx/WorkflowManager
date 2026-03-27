package WorkflowManager.user;

import WorkflowManager.user.model.AddPermissionRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add-permissions")
    public void AddPermission(@RequestBody AddPermissionRequest request) {
        userService.addPermissions(request);
    }
}
