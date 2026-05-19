package com.sutusxxx.graphql.project;

import com.sutusxxx.graphql.exceptions.NotFoundException;
import com.sutusxxx.graphql.issue.Status;
import com.sutusxxx.graphql.issue.model.CreateStatusInput;
import com.sutusxxx.graphql.project.model.CreateProjectInput;
import com.sutusxxx.graphql.project.model.UpdateProjectInput;
import com.sutusxxx.graphql.project.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    private final ProjectConverter projectConverter;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectConverter projectConverter) {
        this.projectRepository = projectRepository;
        this.projectConverter = projectConverter;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(String id) {
        return projectRepository.findById(id).orElseThrow(NotFoundException::new);
    }


    public Project createProject(CreateProjectInput input) {
        Project project = projectConverter.convertFromInput(input);
        project.setVisibility(input.isPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);
        return projectRepository.save(project);
    }

    public Project updateProject(String id, UpdateProjectInput input) {
        Project project = projectRepository.findById(id).orElseThrow();

        if (input.description() != null && !input.description().equals(project.getDescription())) {
            project.setDescription(input.description());
        }

        project.setVisibility(input.isPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);

        return project;
    }

    public Status addStatus(String projectId, CreateStatusInput input) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        boolean nameExists = project.getStatuses().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(input.name()));
        if (nameExists) throw new RuntimeException("Already exists");

        if (input.isDefault()) {
            project.getStatuses().forEach(s -> s.setDefault(false));
        }

        Status status = new Status();
        status.setName(input.name());
        status.setCategory(input.category());
        status.setColor(input.color());
        status.setDisplayOrder(input.displayOrder());
        status.setDefault(input.isDefault());

        project.getStatuses().add(status);
        projectRepository.save(project);
        return status;
    }

    public void addTransition(String projectId, String fromStatusId, String toStatusId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        Status from = project.findStatusById(fromStatusId)
                .orElseThrow(() -> new RuntimeException("'From' status not found"));

        project.findStatusById(toStatusId)
                .orElseThrow(() -> new RuntimeException("'To' status not found"));

        if (!from.getAllowedTransitionIds().contains(toStatusId)) {
            from.getAllowedTransitionIds().add(toStatusId);
            projectRepository.save(project);
        }
    }
}
