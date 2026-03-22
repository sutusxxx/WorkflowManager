package WorkflowManager.issue;

import WorkflowManager.issue.models.CreateIssueRequest;
import WorkflowManager.issue.models.IssueDTO;
import WorkflowManager.issue.models.IssueTreeDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IssueConverter {
    private final ModelMapper mapper;

    public IssueConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public IssueDTO convertToDTO(Issue issue) {
        return mapper.map(issue, IssueDTO.class);
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
