package WorkflowManager.board.dao;

import WorkflowManager.board.Board;
import WorkflowManager.board.Sprint;
import WorkflowManager.common.dao.BaseDAO;

import java.util.List;
import java.util.Optional;

public interface BoardDAO extends BaseDAO<Board, Long> {
    Optional<Sprint> findActiveSprintByProjectKey(String projectKey);
    List<Board> findByProjectKey(String projectKey);
}
