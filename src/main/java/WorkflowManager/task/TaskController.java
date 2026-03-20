package WorkflowManager.task;

import WorkflowManager.exceptions.TaskNotFoundException;
import WorkflowManager.task.dtos.CreateTaskDTO;
import WorkflowManager.task.dtos.TaskDTO;
import WorkflowManager.task.dtos.UpdateTaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/all")
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskDTO getById(@PathVariable Long id) {
        try {
            return taskService.getTaskById(id);
        } catch (TaskNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/create")
    public TaskDTO createTask(@RequestBody CreateTaskDTO task) {
        return taskService.createTask(task);
    }

    @PostMapping("/{id}/update")
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody UpdateTaskDTO task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
