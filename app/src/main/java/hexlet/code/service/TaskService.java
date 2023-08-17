package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

public interface TaskService {
    Task createNew(TaskDto taskDto);
    Task update(TaskDto taskDto, Long id);
    void deleteById(Long id);
}
