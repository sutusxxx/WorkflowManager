package WorkflowManager.project;

import WorkflowManager.exceptions.ProjectNotFoundException;
import WorkflowManager.project.dtos.CreateProjectDTO;
import WorkflowManager.project.dtos.ProjectDTO;
import WorkflowManager.project.dtos.UpdateProjectDTO;
import WorkflowManager.task.TaskConverter;
import WorkflowManager.task.TaskRepository;
import WorkflowManager.task.dtos.TaskDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectConverter projectConverter;
    private final TaskConverter taskConverter;

    public ProjectService(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            ProjectConverter projectConverter,
            TaskConverter taskConverter
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.projectConverter = projectConverter;
        this.taskConverter = taskConverter;
    }

    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow();
        return projectConverter.convertToDTO(project);
    }

    public ProjectDTO createProject(CreateProjectDTO project) {
        Project projectToSave = projectConverter.convertFromDTO(project);
        Project savedProject = projectRepository.save(projectToSave);
        return projectConverter.convertToDTO(savedProject);
    }

    public ProjectDTO updateProject(Long id, UpdateProjectDTO project) {
        Project projectDb = projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);

        if (project.getDescription() != null && !project.getDescription().equals(projectDb.getDescription())) {
            projectDb.setDescription(project.getDescription());
        }

        Project savedProject = projectRepository.save(projectDb);
        return projectConverter.convertToDTO(savedProject);
    }

    public List<TaskDTO> getTasksByProject(Long projectId) {
        return taskRepository.findByProject(projectId).stream().map(taskConverter::convertToDTO).toList();
    }
}
