package WorkflowManager.user;

import WorkflowManager.user.dao.UserDAO;
import WorkflowManager.user.model.UserDetailsDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserDAO userDAO;
    private final UserConverter userConverter;

    public UserService(UserDAO userDAO, UserConverter userConverter) {
        this.userDAO = userDAO;
        this.userConverter = userConverter;
    }

    @Override
    public UserDetailsDTO loadUserByUsername(String username) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
        return userConverter.convertToDTO(user);
    }
}
