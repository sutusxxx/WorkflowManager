package WorkflowManager.issue.dao;

import WorkflowManager.common.exceptions.IssueNotFoundException;
import WorkflowManager.issue.Issue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class IssueDAOImpl implements IssueDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Issue> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Issue.class, id));
    }

    @Override
    public Optional<Issue> findByKey(String key) {
        try {
            Issue issue = entityManager.createQuery("SELECT i FROM Issue i WHERE i.key = :key", Issue.class)
                    .setParameter("key", key)
                    .getSingleResult();
            return Optional.of(issue);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Issue> findAll() {
        return entityManager.createQuery("SELECT i FROM Issue i", Issue.class).getResultList();
    }

    @Override
    public Issue save(Issue entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Override
    public void delete(Issue entity) {
        entityManager.remove(entity);
    }

    @Override
    public void deleteById(Long id) {
        Issue issue = entityManager.find(Issue.class, id);
        entityManager.remove(issue);
    }

    @Override
    public List<Issue> findAllByParentId(Long parentId) {
        return null;
    }

    @Override
    public List<Issue> findByProjectId(Long projectId) {
        return null;
    }
}
