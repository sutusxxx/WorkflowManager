package WorkflowManager.auth;

import WorkflowManager.auth.model.LoginRequest;
import WorkflowManager.auth.model.RegisterRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest request) {

    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {

    }
}
