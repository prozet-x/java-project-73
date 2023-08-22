package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;

    @Override
    public TaskStatus createNew(TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus update(TaskStatusDto taskStatusDto, Long id) {
        checkExisting(id);
        final TaskStatus taskStatus = taskStatusRepository.findById(id).get();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public void deleteById(Long id) {
        checkExisting(id);
        if (!taskRepository.existsByStatus(taskStatusRepository.findById(id).get())) {
            taskStatusRepository.deleteById(id);
        } else {
            throw new DataIntegrityViolationException("There are tasks with a deleted status");
        }
        taskStatusRepository.deleteById(id);
    }

    private void checkExisting(final long id) {
        if (!taskStatusRepository.existsById(id)) {
            throw new NoSuchElementException(String.format("Task status with id %d not found", id));
        }
    }
}
