package WorkflowManager.issue;

import WorkflowManager.issue.dtos.CreateIssueDTO;
import WorkflowManager.issue.dtos.IssueDTO;
import WorkflowManager.issue.dtos.IssueTreeDTO;
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

    public Issue convertFromDTO(CreateIssueDTO taskDTO) {
        return mapper.map(taskDTO, Issue.class);
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
