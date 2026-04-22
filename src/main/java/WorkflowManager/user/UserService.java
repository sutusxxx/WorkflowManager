package WorkflowManager.user;

import WorkflowManager.user.model.UserDetailsDTO;
import WorkflowManager.user.model.UserSummaryDTO;
import WorkflowManager.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final UserConverter userConverter;

    public UserService(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Override
    public UserDetailsDTO loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
        return userConverter.convertToDTO(user);
    }

    public UserSummaryDTO getUserSummaryById(String id) {
        User user = userRepository.findById(id).orElseThrow();
        return userConverter.convertToSummaryDTO(user);
    }
}
