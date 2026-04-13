package WorkflowManager.auth;

import WorkflowManager.auth.model.LoginRequest;
import WorkflowManager.auth.model.RegisterRequest;
import WorkflowManager.user.User;
import WorkflowManager.user.model.UserInfoDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtility jwtUtility;

    @Autowired
    public AuthController(AuthService authService, JwtUtility jwtUtility) {
        this.authService = authService;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping("/authenticate")
    public UserInfoDTO authenticate(@RequestBody LoginRequest request, HttpServletResponse response) {
        User user = authService.authenticate(request);

        String accessToken = jwtUtility.generateToken(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from("session_token", accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(900)
                .sameSite(SameSiteCookies.LAX.toString())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return authService.getCurrent(user);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }
}
