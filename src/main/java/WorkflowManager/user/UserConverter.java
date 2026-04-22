package WorkflowManager.user;

import WorkflowManager.user.model.UserDetailsDTO;
import WorkflowManager.user.model.UserSummaryDTO;
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

    public UserSummaryDTO convertToSummaryDTO(User user) {
        return mapper.map(user, UserSummaryDTO.class);
    }
}
