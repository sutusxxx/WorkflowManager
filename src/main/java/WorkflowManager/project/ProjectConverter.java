package WorkflowManager.project;

import WorkflowManager.project.model.CreateProjectRequest;
import WorkflowManager.project.model.ProjectDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectConverter {
    private final ModelMapper mapper;

    public ProjectConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public ProjectDTO convertToDTO(Project project) {
        return mapper.map(project, ProjectDTO.class);
    }

    public Project convertFromRequest(CreateProjectRequest request) {
        return mapper.map(request, Project.class);
    }
}
