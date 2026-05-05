package WorkflowManager.auth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @GetMapping("/test")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('role_user')")
    public String user() {
        return "user";
    }
}
