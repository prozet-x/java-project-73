package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService{
    private TaskRepository taskRepository;

    @Override
    public Task createNew(TaskDto taskDto) {
        final Task task = new Task();
        task.setName(taskDto.getName());
        task.setAuthor(taskDto.getAuthor());
        task.setDescr(taskDto.getDescr());
        task.setExecutor(taskDto.getExecutor());
        task.setStatus(taskDto.getStatus());
        return taskRepository.save(task);
    }

    @Override
    public Task update(TaskDto taskDto, Long id) {
        checkExisting(id);
        final Task task = taskRepository.findById(id).get();
        task.setName(taskDto.getName());
        task.setAuthor(taskDto.getAuthor());
        task.setDescr(taskDto.getDescr());
        task.setExecutor(taskDto.getExecutor());
        task.setStatus(taskDto.getStatus());
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
}
