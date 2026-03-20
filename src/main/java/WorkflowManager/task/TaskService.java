package WorkflowManager.task;

import WorkflowManager.exceptions.TaskNotFoundException;
import WorkflowManager.task.dtos.CreateTaskDTO;
import WorkflowManager.task.dtos.TaskDTO;
import WorkflowManager.task.dtos.UpdateTaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskConverter taskConverter;

    @Autowired
    public TaskService(
            TaskRepository taskRepository,
            TaskConverter taskConverter
    ) {
        this.taskRepository = taskRepository;
        this.taskConverter = taskConverter;
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(taskConverter::convertToDTO).toList();
    }

    public TaskDTO getTaskById(Long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        return taskConverter.convertToDTO(task);
    }

    public TaskDTO createTask(CreateTaskDTO task) {
        Task taskToSave = taskConverter.convertFromDTO(task);
        taskToSave.setStatus("TODO");
        Task savedTask = taskRepository.save(taskToSave);
        return taskConverter.convertToDTO(savedTask);
    }

    public TaskDTO updateTask(Long id, UpdateTaskDTO task) {
        Task taskDb = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);

        if (task.getTitle() != null) {
            taskDb.setTitle(task.getTitle());
        }

        if (task.getStatus() != null) {
            taskDb.setStatus(task.getStatus());
        }

        Task savedTask = taskRepository.save(taskDb);
        return taskConverter.convertToDTO(savedTask);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
