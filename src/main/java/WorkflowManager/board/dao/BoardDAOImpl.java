package WorkflowManager.board.dao;

import WorkflowManager.board.Board;
import WorkflowManager.board.Sprint;

import java.util.List;
import java.util.Optional;

public class BoardDAOImpl implements BoardDAO {

    @Override
    public Optional<Sprint> findActiveSprintByProjectKey(String projectKey) {
        return Optional.empty();
    }

    @Override
    public List<Board> findByProjectKey(String projectKey) {
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
