package hexlet.code.controller;

import com.rollbar.notifier.Rollbar;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;

@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
@AllArgsConstructor
public class LabelController {
    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";
    private LabelRepository labelRepository;
    private LabelService labelService;
    private Rollbar rollbar;

    @Operation(summary = "Create new label")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "New label created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Label.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "422", description = "Bad input data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Label createNew(@RequestBody @Valid @Parameter(description = "Label to create") final LabelDto labelDto) {
        return labelService.createNew(labelDto);
    }

    @Operation(summary = "Get all labels")
    @ApiResponse(
            responseCode = "200",
            description = "All labels got",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Label.class)))
            }
    )
    @GetMapping
    public List<Label> getAll() {
        return labelRepository.findAll();
    }

    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Label got",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Label.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "Label with given id not found")
    })
    @GetMapping(ID)
    public Label getById(@PathVariable @Parameter(description = "Id of label to get") final Long id) {
        return labelRepository.findById(id).get();
    }

    @Operation(summary = "Update label by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Label updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Label.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "Label with given id not found"),
            @ApiResponse(responseCode = "422", description = "Bad input data")
    })
    @PutMapping(ID)
    public Label update(
            @RequestBody @Valid @Parameter(description = "New label data") final LabelDto labelDto,
            @PathVariable @Parameter(description = "Id of label to update") final long id) {
        return labelService.update(labelDto, id);
    }

    @Operation(summary = "Delete label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted"),
            @ApiResponse(responseCode = "404", description = "Label with given id not found")
    })
    @DeleteMapping(ID)
    public void delete(@PathVariable @Parameter(description = "Id of label to delete") final Long id) {
        labelService.deleteById(id);
    }
}
