package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    private final String ID = "/{id}";

    private static final String ONLY_CREATOR_BY_TASK_ID = "@taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()";

    private TaskRepository taskRepository;
    private TaskService taskService;

    @GetMapping(ID)
    public Task getById(@PathVariable final Long id) {
        return taskRepository.findById(id).get();
    }

    @GetMapping
    public List<Task> getAll(@QuerydslPredicate(root = Task.class) Predicate predicate, @PageableDefault Pageable pageable) {
        return taskRepository.findAll(predicate, pageable).getContent();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createNew(@RequestBody final @Valid TaskDto taskDto) {
//        SecurityContextHolder.getContext().getAuthentication()
        return taskService.createNew(taskDto);
    }

    @PutMapping(ID)
    public Task update(@RequestBody @Valid final TaskDto taskDto, @PathVariable final Long id) {
        return taskService.update(taskDto, id);
    }

    @DeleteMapping(ID)
    @PreAuthorize(ONLY_CREATOR_BY_TASK_ID)
    public void delete(@PathVariable final Long id) {
        taskService.deleteById(id);
    }
}
