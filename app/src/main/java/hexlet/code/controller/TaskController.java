package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.data.domain.Pageable;
import java.util.List;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    private static final String ID = "/{id}";

    private static final String ONLY_CREATOR_BY_TASK_ID =
            "@taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()";

    private TaskRepository taskRepository;
    private TaskService taskService;

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
        @ApiResponse(
                    responseCode = "200",
                    description = "Task got",
                    content = {
                        @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Task.class)
                            )
                    }
            ),
        @ApiResponse(responseCode = "404", description = "Task with given id not found")
    })
    @GetMapping(ID)
    public Task getById(@PathVariable @Parameter(description = "Id of task to get") final Long id) {
        return taskRepository.findById(id).get();
    }

    @Operation(summary = "Get all tasks")
    @ApiResponse(
            responseCode = "200",
            description = "All tasks got",
            content = {
                @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Task.class)))
            }
    )
    @GetMapping
    public List<Task> getAll(
            @QuerydslPredicate(root = Task.class)
            @Parameter(description = "Predicate to filter tasks") Predicate predicate,

            @PageableDefault
            @Parameter(description = "Contains pagination parameters") Pageable pageable) {
        return taskRepository.findAll(predicate, pageable).getContent();
    }

    @Operation(summary = "Create new task")
    @ApiResponses(value = {
        @ApiResponse(
                    responseCode = "201",
                    description = "New task created",
                    content = {
                        @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Task.class)
                            )
                    }
            ),
        @ApiResponse(responseCode = "422", description = "Bad input data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createNew(@RequestBody @Parameter(description = "Task to create") final @Valid TaskDto taskDto) {
        return taskService.createNew(taskDto);
    }

    @Operation(summary = "Update task by id")
    @ApiResponses(value = {
        @ApiResponse(
                    responseCode = "200",
                    description = "Task updated",
                    content = {
                        @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Task.class)
                            )
                    }
            ),
        @ApiResponse(responseCode = "404", description = "Task with given id not found"),
        @ApiResponse(responseCode = "422", description = "Bad input data")
    })
    @PutMapping(ID)
    public Task update(
            @RequestBody @Valid @Parameter(description = "New task data") final TaskDto taskDto,
            @PathVariable @Parameter(description = "Id of task to update") final Long id) {
        return taskService.update(taskDto, id);
    }

    @Operation(summary = "Delete task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted"),
        @ApiResponse(responseCode = "404", description = "Task with given id not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_CREATOR_BY_TASK_ID)
    public void delete(@PathVariable @Parameter(description = "Id of label to delete") final Long id) {
        taskService.deleteById(id);
    }
}
