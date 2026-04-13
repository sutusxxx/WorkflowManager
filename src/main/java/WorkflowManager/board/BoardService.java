package WorkflowManager.board;

import WorkflowManager.board.dao.BoardDAO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {
    private final BoardDAO boardDAO;

    public BoardService(BoardDAO boardDAO) {
        this.boardDAO = boardDAO;
    }

    public Sprint getActiveSprintByProjectId(Long projectId) {
        return boardDAO.findActiveSprintByProjectId(projectId).orElse(null);
    }
}
