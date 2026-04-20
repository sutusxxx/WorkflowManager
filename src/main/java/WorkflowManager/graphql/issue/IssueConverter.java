package WorkflowManager.graphql.issue;

import WorkflowManager.graphql.issue.model.CreateIssueInput;
import WorkflowManager.graphql.issue.model.IssueDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class IssueConverter {
    private final ModelMapper mapper;

    public IssueConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Issue convertFromInput(CreateIssueInput input) {
        return mapper.map(input, Issue.class);
    }

    public IssueDTO convertToDTO(Issue entity) {
        return mapper.map(entity, IssueDTO.class);
    }
}
