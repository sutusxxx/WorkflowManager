package WorkflowManager.project;

import WorkflowManager.project.dtos.CreateProjectDTO;
import WorkflowManager.project.dtos.ProjectDTO;
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

    public Project convertFromDTO(CreateProjectDTO projectDTO) {
        return mapper.map(projectDTO, Project.class);
    }
}
