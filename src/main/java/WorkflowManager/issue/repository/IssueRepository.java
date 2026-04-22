package WorkflowManager.issue.repository;

import WorkflowManager.issue.Issue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IssueRepository extends MongoRepository<Issue, String> {
    Optional<Issue> findByKey(String key);

    @Query("{ 'projectId': ?0, 'type': { $ne: 'SUBTASK' } }")
    List<Issue> findByProjectId(String projectId);
    List<Issue> findByParentId(String parentId);
    List<Issue> findByParentIdIn(Collection<String> parentIds);
    List<Issue> findByProjectIdAndStatusId(String projectId, String statusId);
    List<Issue> findByProjectIdAndAssignee(String projectId, String assignee);
    boolean existsByStatusId(String statusId);
}
