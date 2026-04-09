package WorkflowManager.board.dao;

import WorkflowManager.board.Board;
import WorkflowManager.board.Sprint;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BoardDAOImpl implements BoardDAO {

    @Override
    public Optional<Sprint> findActiveSprintByProjectId(Long projectId) {
        return Optional.empty();
    }

    @Override
    public List<Board> findByProjectId(Long projectId) {
        return List.of();
    }

    @Override
    public Optional<Board> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Board> findAll() {
        return List.of();
    }

    @Override
    public Board save(Board entity) {
        return null;
    }

    @Override
    public void delete(Board entity) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
}
