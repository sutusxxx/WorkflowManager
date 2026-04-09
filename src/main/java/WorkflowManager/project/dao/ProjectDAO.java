package WorkflowManager.project.dao;

import WorkflowManager.common.dao.BaseDAO;
import WorkflowManager.project.Project;

import java.util.Optional;

public interface ProjectDAO extends BaseDAO<Project, Long> {
    Optional<Project> findByIdForUpdate(Long id);
}
