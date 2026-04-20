package WorkflowManager.graphql.project.repository;

import WorkflowManager.graphql.project.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {
}
