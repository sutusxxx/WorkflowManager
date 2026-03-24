package WorkflowManager.auth;

import WorkflowManager.auth.model.AuthenticationResponseDTO;
import WorkflowManager.auth.model.LoginRequest;
import WorkflowManager.auth.model.RegisterRequest;
import WorkflowManager.user.User;
import WorkflowManager.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtility jwtUtility, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtility = jwtUtility;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponseDTO authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        String accessToken = jwtUtility.generateToken(user.getUsername());

        return new AuthenticationResponseDTO(accessToken);
    }

    public AuthenticationResponseDTO register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String accessToken = jwtUtility.generateToken(user.getUsername());

        return new AuthenticationResponseDTO(accessToken);
    }
}
