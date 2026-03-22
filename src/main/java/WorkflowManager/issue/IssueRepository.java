package WorkflowManager.issue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    List<Issue> findByProject(Long projectId);
    List<Issue> findAllByParent(Long parentId);

    @Query("SELECT i FROM Issue i WHERE i.project.id = :projectId AND i.parent IS NULL")
    List<Issue> findFirstLevelIssuesOnProject(Long projectId);
    Optional<Issue> findByKey(String key);
}
