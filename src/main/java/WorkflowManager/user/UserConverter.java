package WorkflowManager.user;

import WorkflowManager.user.model.UserDetailsDTO;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserDetailsDTO convertToDTO(User user) {
        return new UserDetailsDTO(user);
    }
}
