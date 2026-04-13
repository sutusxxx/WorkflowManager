package WorkflowManager.project;

import WorkflowManager.project.model.CreateProjectInput;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectConverter {
    private final ModelMapper mapper;

    public ProjectConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Project convertFromInput(CreateProjectInput input) {
        return mapper.map(input, Project.class);
    }
}
