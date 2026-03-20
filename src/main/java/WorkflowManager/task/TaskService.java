package WorkflowManager.task;

import WorkflowManager.exceptions.TaskNotFoundException;
import WorkflowManager.task.dtos.CreateTaskDTO;
import WorkflowManager.task.dtos.TaskDTO;
import WorkflowManager.task.dtos.UpdateTaskDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TaskService(
            TaskRepository taskRepository,
            ModelMapper modelMapper
    ) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public TaskDTO getTaskById(Long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        return convertToDTO(task);
    }

    public TaskDTO createTask(CreateTaskDTO task) {
        Task taskToSave = convertFromDTO(task);
        System.out.println(taskToSave);
        taskToSave.setStatus("Open");
        Task savedTask = taskRepository.save(taskToSave);
        return convertToDTO(savedTask);
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
        return convertToDTO(savedTask);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private TaskDTO convertToDTO(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }

    private Task convertFromDTO(CreateTaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }
}
