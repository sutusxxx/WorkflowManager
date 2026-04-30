package WorkflowManager.auth;

import WorkflowManager.auth.model.LoginRequest;
import WorkflowManager.auth.model.RegisterRequest;
import WorkflowManager.user.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtility jwtUtility;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthService authService, JwtUtility jwtUtility) {
        this.authService = authService;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(@RequestBody LoginRequest request, HttpServletResponse response) {
        User user = authService.authenticate(request);
        String accessToken = jwtUtility.generateToken(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from("session_token", accessToken)
                .httpOnly(true)
                .path("/")
                .sameSite(SameSiteCookies.LAX.toString())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("session '{}' created", cookie);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("session_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal UserDetails currentUser) {
        return currentUser.getUsername();
    }
}
