package WorkflowManager.user;

import WorkflowManager.permission.Permission;
import WorkflowManager.user.model.UserDetailsDTO;
import WorkflowManager.user.model.UserInfoDTO;
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

    public UserInfoDTO convertToInfoDTO(User user) {
        UserInfoDTO dto = mapper.map(user, UserInfoDTO.class);
        dto.setPermissions(user.getPermissions().stream().map(Permission::getName).toList());
        return dto;
    }
}
