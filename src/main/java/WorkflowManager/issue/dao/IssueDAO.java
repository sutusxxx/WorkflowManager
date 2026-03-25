package WorkflowManager.issue.dao;

import WorkflowManager.common.dao.BaseDAO;
import WorkflowManager.issue.Issue;

import java.util.List;
import java.util.Optional;

public interface IssueDAO extends BaseDAO<Issue, Long> {
    Optional<Issue> findByKey(String key);
    List<Issue> findFirstLevelByProjectId(Long projectId);
    List<Issue> findAllByParentId(Long parentId);
    List<Issue> findByProjectId(Long projectId);
    void deleteByKey(String key);
}
