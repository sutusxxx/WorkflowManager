package WorkflowManager.project.repository;

import WorkflowManager.project.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {
}
