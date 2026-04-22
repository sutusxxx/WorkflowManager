package WorkflowManager.auth;

import WorkflowManager.user.repository.UserRepository;
import WorkflowManager.common.exceptions.UnauthorizedException;
import WorkflowManager.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {
    private final UserRepository userRepository;

    public AuthContext(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username).orElseThrow(UnauthorizedException::new);
    }
}
