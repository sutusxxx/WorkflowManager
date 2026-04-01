package WorkflowManager.auth;

import WorkflowManager.auth.model.AuthenticationResponseDTO;
import WorkflowManager.auth.model.LoginRequest;
import WorkflowManager.auth.model.RegisterRequest;
import WorkflowManager.user.User;
import WorkflowManager.user.UserConverter;
import WorkflowManager.user.dao.UserDAO;
import WorkflowManager.user.model.UserInfoDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserDAO userDAO;

    private final UserConverter userConverter;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;
    private final AuthenticationManager authenticationManager;
    private final AuthContext authContext;

    public AuthService(UserDAO userDAO, UserConverter userConverter, PasswordEncoder passwordEncoder, JwtUtility jwtUtility, AuthenticationManager authenticationManager, AuthContext authContext) {
        this.userDAO = userDAO;
        this.userConverter = userConverter;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtility = jwtUtility;
        this.authenticationManager = authenticationManager;
        this.authContext = authContext;
    }

    public AuthenticationResponseDTO authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userDAO.findByUsername(request.getUsername()).orElseThrow();

        String accessToken = jwtUtility.generateToken(user.getUsername());

        return new AuthenticationResponseDTO(accessToken, user.getUsername(), user.getEmail());
    }

    public AuthenticationResponseDTO register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userDAO.save(user);

        String accessToken = jwtUtility.generateToken(user.getUsername());

        return new AuthenticationResponseDTO(accessToken, user.getUsername(), user.getEmail());
    }

    public UserInfoDTO getCurrent() {
        User currentUser = authContext.getCurrentUser();
        return userConverter.convertToInfoDTO(currentUser);
    }
}
