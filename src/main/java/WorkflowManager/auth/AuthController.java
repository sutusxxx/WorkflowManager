package WorkflowManager.auth;

import WorkflowManager.auth.model.AuthenticationResponseDTO;
import WorkflowManager.auth.model.LoginRequest;
import WorkflowManager.auth.model.RegisterRequest;
import WorkflowManager.user.model.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthenticationResponseDTO authenticate(@RequestBody LoginRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/register")
    public AuthenticationResponseDTO register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/current")
    public UserInfoDTO getCurrentUserInfo() {
        return authService.getCurrent();
    }
}
