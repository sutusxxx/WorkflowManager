package WorkflowManager.task;

import WorkflowManager.task.dtos.CreateTaskDTO;
import WorkflowManager.task.dtos.TaskDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskConverter {
    private final ModelMapper mapper;

    public TaskConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public TaskDTO convertToDTO(Task task) {
        return mapper.map(task, TaskDTO.class);
    }

    public Task convertFromDTO(CreateTaskDTO taskDTO) {
        return mapper.map(taskDTO, Task.class);
    }
}
