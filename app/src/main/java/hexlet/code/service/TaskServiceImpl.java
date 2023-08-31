package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService{
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TaskStatusRepository taskStatusRepository;
    private LabelRepository labelRepository;

    @Override
    public Task createNew(TaskDto taskDto) {
        String emailOfCurUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Long idOfCurrentUser = userRepository.findByEmail(emailOfCurUser).get().getId();
        final Task task = new Task();
        taskDto.setAuthorId(idOfCurrentUser);
        setTaskFromTaskDto(task, taskDto);
        return taskRepository.save(task);
    }

    @Override
    public Task update(TaskDto taskDto, Long id) {
        checkExisting(id);
        final Task task = taskRepository.findById(id).get();
        taskDto.setAuthorId(task.getAuthor().getId());
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
        task.setDescription(taskDto.getDescription());
        task.setExecutor(userRepository.findById(taskDto.getExecutorId()).get());
        task.setTaskStatus(taskStatusRepository.findById(taskDto.getTaskStatusId()).get());
        task.setLabels(getLabelsFromListOfLabelsIds(taskDto.getLabelIds()));
    }

    private List<Label> getLabelsFromListOfLabelsIds(List<Long> ids) {
        return ids.stream()
                .map(id -> labelRepository.findById(id).get())
                .collect(Collectors.toList());
    }
}
