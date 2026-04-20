package WorkflowManager.auth;

import WorkflowManager.auth.model.UserDetailsDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    private final ModelMapper mapper;

    public UserConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public UserDetailsDTO convertToDTO(User user) {
        return new UserDetailsDTO(user);
    }
}
