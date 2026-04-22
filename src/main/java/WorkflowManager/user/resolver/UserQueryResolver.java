package WorkflowManager.user.resolver;

import WorkflowManager.user.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserQueryResolver {
    private final UserService userService;

    public UserQueryResolver(UserService userService) {
        this.userService = userService;
    }
}
