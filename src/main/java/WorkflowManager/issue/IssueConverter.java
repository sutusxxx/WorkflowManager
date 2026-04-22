package WorkflowManager.issue;

import WorkflowManager.issue.model.CreateIssueInput;
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
}
