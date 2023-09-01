package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Create new label")
    @ApiResponse(responseCode = "201", description = "New label created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Label createNew(@RequestBody @Valid final LabelDto labelDto) {
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
    @ApiResponse(responseCode = "200",
            description = "Label got",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Label.class)
                    )
            }
    )
    @GetMapping(ID)
    public Label getById(@PathVariable final Long id) {
        return labelRepository.findById(id).get();
    }

    @PutMapping(ID)
    public Label update(@RequestBody @Valid final LabelDto labelDto, @PathVariable final long id) {
        return labelService.update(labelDto, id);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final Long id) {
        labelService.deleteById(id);
    }
}
