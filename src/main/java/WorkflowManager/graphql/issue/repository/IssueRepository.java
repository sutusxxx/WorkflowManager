package WorkflowManager.graphql.issue.repository;

import WorkflowManager.graphql.issue.Issue;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends MongoRepository<Issue, String> {
    Optional<Issue> findByKey(String key);
    List<Issue> findByProjectId(String projectId);
    List<Issue> findByParentId(String parentId);
    List<Issue> findByProjectIdAndStatusId(String projectId, String statusId);
    List<Issue> findByProjectIdAndAssignee(String projectId, String assignee);
    boolean existsByStatusId(String statusId);
}
