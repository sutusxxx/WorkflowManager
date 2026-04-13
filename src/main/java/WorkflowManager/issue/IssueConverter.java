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

    public Issue convertFromInput(CreateIssueInput input) {
        return mapper.map(input, Issue.class);
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
