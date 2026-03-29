package WorkflowManager.permission.dao;

import WorkflowManager.permission.Permission;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PermissionDAOImpl implements PermissionDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Permission> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Permission> findAll() {
        return List.of();
    }

    @Override
    public Permission save(Permission entity) {
        if (entity.getId() != null) {
            entityManager.persist(entity);
            return entity;
        }
        return entityManager.merge(entity);
    }

    @Override
    public void delete(Permission entity) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
