package hexlet.code.controller;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {
    public static final String TASK_CONTROLLER_PATH = "/task";
    private final String ID = "/{id}";

    private TaskRepository taskRepository;
    private TaskService taskService;

    @GetMapping(ID)
    public Task getById(@PathVariable final Long id) {
        return taskRepository.findById(id).get();
    }

    @GetMapping
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @PostMapping
    public Task createNew(@RequestBody final TaskDto taskDto) {
        return taskService.createNew(taskDto);
    }

    @PutMapping(ID)
    public Task update(@RequestBody final TaskDto taskDto, @PathVariable final Long id) {
        return taskService.update(taskDto, id);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final Long id) {
        taskService.deleteById(id);
    }
}
