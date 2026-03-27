package WorkflowManager.user;

import WorkflowManager.auth.AuthContext;
import WorkflowManager.user.dao.UserDAO;
import WorkflowManager.user.model.AddPermissionRequest;
import WorkflowManager.user.model.UserDetailsDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserDAO userDAO;

    private final UserConverter userConverter;

    private final AuthContext authContext;

    public UserService(UserDAO userDAO, UserConverter userConverter, AuthContext authContext) {
        this.userDAO = userDAO;
        this.userConverter = userConverter;
        this.authContext = authContext;
    }

    @Override
    public UserDetailsDTO loadUserByUsername(String username) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
        return userConverter.convertToDTO(user);
    }

    @Transactional
    public void addPermissions(AddPermissionRequest request) {
        User user = userDAO.findById(request.getUserId()).orElseThrow();
        Set<Permission> permissions = request.getPermissions().stream()
                .map(String::toUpperCase)
                .map(Permission::valueOf)
                .collect(Collectors.toSet());
        permissions.addAll(user.getPermissions());
        user.setPermissions(permissions);
    }
}
