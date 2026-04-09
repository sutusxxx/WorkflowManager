package WorkflowManager.board;

import WorkflowManager.board.dao.BoardDAO;
import WorkflowManager.board.model.SprintDTO;
import WorkflowManager.common.exceptions.BoardNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {
    private final BoardDAO boardDAO;

    public BoardService(BoardDAO boardDAO) {
        this.boardDAO = boardDAO;
    }

    public SprintDTO getActiveSprintByProjectId(Long projectId) {
        Optional<Sprint> activeSprint = boardDAO.findActiveSprintByProjectId(projectId);

        if (activeSprint.isEmpty()) return null;

        return null;
    }
}
