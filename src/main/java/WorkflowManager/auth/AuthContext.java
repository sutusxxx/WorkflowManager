package WorkflowManager.auth;

import WorkflowManager.common.exceptions.UnauthorizedException;
import WorkflowManager.user.User;
import WorkflowManager.user.dao.UserDAO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {
    private final UserDAO userDAO;

    public AuthContext(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }

        String username = authentication.getName();

        return userDAO.findByUsername(username).orElseThrow(UnauthorizedException::new);
    }
}
