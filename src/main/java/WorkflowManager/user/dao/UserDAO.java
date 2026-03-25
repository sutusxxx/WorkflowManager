package WorkflowManager.user.dao;

import WorkflowManager.common.dao.BaseDAO;
import WorkflowManager.user.User;

import java.util.Optional;

public interface UserDAO extends BaseDAO<User, Long> {
    Optional<User> findByUsername(String username);
}
