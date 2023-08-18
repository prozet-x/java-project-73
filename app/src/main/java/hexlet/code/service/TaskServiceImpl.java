package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService{
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TaskStatusRepository taskStatusRepository;
    @Override
    public Task createNew(TaskDto taskDto) {
        final Task task = new Task();
        setTaskFromTaskDto(task, taskDto);
        return taskRepository.save(task);
    }

    @Override
    public Task update(TaskDto taskDto, Long id) {
        checkExisting(id);
        final Task task = taskRepository.findById(id).get();
        setTaskFromTaskDto(task, taskDto);
        return taskRepository.save(task);
    }

    @Override
    public void deleteById(Long id) {
        checkExisting(id);
        taskRepository.deleteById(id);
    }

    private void checkExisting(final long id) {
        if (!taskRepository.existsById(id)) {
            throw new NoSuchElementException(String.format("Task with id %d not found", id));
        }
    }

    private void setTaskFromTaskDto(final Task task, final TaskDto taskDto) {
        task.setName(taskDto.getName());
        task.setAuthor(userRepository.findById(taskDto.getAuthorId()).get());
        task.setDescr(taskDto.getDescr());
        task.setExecutor(userRepository.findById(taskDto.getExecutorId()).get());
        task.setStatus(taskStatusRepository.findById(taskDto.getStatusId()).get());
    }
}
