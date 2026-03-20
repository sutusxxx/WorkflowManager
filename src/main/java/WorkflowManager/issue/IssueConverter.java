package WorkflowManager.issue;

import WorkflowManager.issue.dtos.CreateIssueDTO;
import WorkflowManager.issue.dtos.IssueDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

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
}
