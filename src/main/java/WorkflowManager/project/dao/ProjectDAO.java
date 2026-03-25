package WorkflowManager.project.dao;

import WorkflowManager.common.dao.BaseDAO;
import WorkflowManager.project.Project;

import java.util.Optional;

public interface ProjectDAO extends BaseDAO<Project, Long> {
    Optional<Project> findByKey(String key);
    Optional<Project> findByKeyForUpdate(String key);
}
