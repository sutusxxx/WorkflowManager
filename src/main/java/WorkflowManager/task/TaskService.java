package WorkflowManager.task;

import WorkflowManager.exceptions.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) throws TaskNotFoundException {
        return taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
    }
}
