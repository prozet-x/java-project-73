package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;

@RestController
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
@AllArgsConstructor
public class TaskStatusController {
    public static final String STATUS_CONTROLLER_PATH = "/statuses";
    private static final String ID = "/{id}";
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusService taskStatusService;

    @GetMapping
    public List<TaskStatus> getAll() {
        return taskStatusRepository.findAll();
    }

    @GetMapping(ID)
    public TaskStatus getById(@PathVariable final long id) {
        return taskStatusRepository.findById(id).get();
    }

    @PostMapping
    public TaskStatus createNew(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.createNew(taskStatusDto);
    }

    @PutMapping(ID)
    public TaskStatus update(@RequestBody @Valid TaskStatusDto taskStatusDto, @PathVariable final long id) {
        return taskStatusService.update(taskStatusDto, id);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final long id) {
        taskStatusService.deleteById(id);
    }
}
