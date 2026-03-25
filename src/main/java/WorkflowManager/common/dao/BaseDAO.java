package WorkflowManager.common.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);
    void delete(T entity);
    void deleteById(ID id);
}
