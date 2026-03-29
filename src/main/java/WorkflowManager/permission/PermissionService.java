package WorkflowManager.permission;

import WorkflowManager.common.exceptions.UserNotFoundException;
import WorkflowManager.permission.dao.PermissionDAO;
import WorkflowManager.permission.model.AssignPermissionRequest;
import WorkflowManager.permission.model.CreatePermissionRequest;
import WorkflowManager.permission.model.PermissionDTO;
import WorkflowManager.user.User;
import WorkflowManager.user.dao.UserDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionService {
    private final UserDAO userDAO;
    private final PermissionDAO permissionDAO;

    public PermissionService(UserDAO userDAO, PermissionDAO permissionDAO) {
        this.userDAO = userDAO;
        this.permissionDAO = permissionDAO;
    }

    @Transactional
    public void assign(AssignPermissionRequest request) {
        User user = userDAO.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        Set<Permission> permissions = request.getPermissions().stream()
                .map(String::toUpperCase)
                .map(Permission::valueOf)
                .collect(Collectors.toSet());
        user.setPermissions(permissions);
    }

    @Transactional
    public PermissionDTO create(CreatePermissionRequest request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        Permission savedPermission = permissionDAO.save(permission);
        PermissionDTO dto = new PermissionDTO();
        dto.setName(savedPermission.getName());
        dto.setCreatedAt(savedPermission.getCreatedAt());
        return dto;
    }
}
