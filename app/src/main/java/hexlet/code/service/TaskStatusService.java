package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;

public interface TaskStatusService {
    TaskStatus createNew(TaskStatusDto taskStatusDto);
    TaskStatus update(TaskStatusDto taskStatusDto, Long id);
    void deleteById(Long id);
}
