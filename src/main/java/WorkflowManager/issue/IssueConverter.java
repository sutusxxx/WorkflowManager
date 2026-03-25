package WorkflowManager.issue;

import WorkflowManager.issue.model.*;
import WorkflowManager.user.model.UserSummaryDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IssueConverter {
    private final ModelMapper mapper;

    public IssueConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public IssueSummaryDTO convertToSummaryDTO(Issue issue) {
        return mapper.map(issue, IssueSummaryDTO.class);
    }

    public IssueDTO convertToDTO(Issue issue) {
        IssueDTO dto = mapper.map(issue, IssueDTO.class);
        List<IssueSummaryDTO> subIssues = issue.getChildren().stream()
                .map(this::convertToSummaryDTO)
                .toList();
        dto.setSubIssues(subIssues);

        if (issue.getParent() != null) {
            dto.setParentKey(issue.getParent().getKey());
        }

        dto.setProjectKey(issue.getProject().getKey());
        List<CommentDTO> commentDTOs = issue.getComments().stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setText(comment.getText());
            commentDTO.setCreatedAt(comment.getCreatedAt());
            UserSummaryDTO userSummaryDTO = new UserSummaryDTO();
            userSummaryDTO.setId(comment.getCreatedBy().getId());
            userSummaryDTO.setUsername(comment.getCreatedBy().getUsername());
            commentDTO.setCreatedBy(userSummaryDTO);
            return commentDTO;
        }).toList();
        dto.setComments(commentDTOs);
        return dto;
    }

    public Issue convertFromRequest(CreateIssueRequest request) {
        return mapper.map(request, Issue.class);
    }

    public IssueTreeDTO convertToTreeDTO(Issue issue) {
        IssueTreeDTO dto = mapper.map(issue, IssueTreeDTO.class);
        List<IssueTreeDTO> children = issue.getChildren().stream()
                .map(this::convertToTreeDTO)
                .toList();
        dto.setChildren(children);
        return dto;
    }
}
