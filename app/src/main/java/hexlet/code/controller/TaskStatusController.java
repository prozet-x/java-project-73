package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    @Operation(summary = "Get all task statuses")
    @ApiResponse(
            responseCode = "200",
            description = "All task statuses got",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TaskStatus.class)))
            }
    )
    @GetMapping
    public List<TaskStatus> getAll() {
        return taskStatusRepository.findAll();
    }

    @Operation(summary = "Get task status by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task status got",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskStatus.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "Task status with given id not found")
    })
    @GetMapping(ID)
    public TaskStatus getById(@PathVariable @Parameter(description = "Id of task status to get") final long id) {
        return taskStatusRepository.findById(id).get();
    }

    @Operation(summary = "Create new task status")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "New task status created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskStatus.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "422", description = "Bad input data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createNew(
            @RequestBody
            @Valid
            @Parameter(description = "Task status to create") TaskStatusDto taskStatusDto) {
        return taskStatusService.createNew(taskStatusDto);
    }

    @Operation(summary = "Update task status by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task status updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskStatus.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "Task status with given id not found"),
            @ApiResponse(responseCode = "422", description = "Bad input data"),
    })
    @PutMapping(ID)
    public TaskStatus update(
            @RequestBody @Valid @Parameter(description = "New task status data") final TaskStatusDto taskStatusDto,
            @PathVariable @Parameter(description = "Id of task status to update") final long id) {
        return taskStatusService.update(taskStatusDto, id);
    }

    @Operation(summary = "Delete task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status deleted"),
            @ApiResponse(responseCode = "404", description = "Task status with given id not found")
    })
    @DeleteMapping(ID)
    public void delete(@PathVariable @Parameter(description = "Id of task status to delete") final long id) {
        taskStatusService.deleteById(id);
    }
}
