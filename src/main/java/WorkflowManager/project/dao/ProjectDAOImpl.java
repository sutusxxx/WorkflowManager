package WorkflowManager.project.dao;

import WorkflowManager.project.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProjectDAOImpl implements ProjectDAO {
    private EntityManager entityManager;

    public ProjectDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Project> findByKey(String key) {
        try {
            Project project = entityManager.createQuery("SELECT p FROM Project p WHERE p.key = :key", Project.class)
                    .setParameter("key", key)
                    .getSingleResult();
            return Optional.of(project);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Project> findByKeyForUpdate(String key) {
        try {
            Project project = entityManager.createQuery("SELECT p FROM Project p WHERE p.key = :key", Project.class)
                    .setParameter("key", key)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();
            return Optional.of(project);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Project> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Project> findAll() {
        return entityManager.createQuery("SELECT p FROM Project p", Project.class).getResultList();
    }

    @Override
    public Project save(Project entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        }
        return entityManager.merge(entity);
    }

    @Override
    public void delete(Project entity) {
        entityManager.remove(entity);
    }

    @Override
    public void deleteById(Long id) {
        Project project = entityManager.find(Project.class, id);
        entityManager.remove(project);
    }
}
