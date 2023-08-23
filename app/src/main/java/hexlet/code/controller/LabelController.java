package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

    @PostMapping
    public Label createNew(@RequestBody @Valid final LabelDto labelDto) {
        return labelService.createNew(labelDto);
    }

    @GetMapping
    public List<Label> getAll() {
        return labelRepository.findAll();
    }

    @GetMapping(ID)
    public Label getById(@PathVariable final Long id) {
        return labelRepository.findById(id).get();
    }

    @PutMapping(ID)
    public Label update(@RequestBody @Valid final LabelDto labelDto, @PathVariable final Long id) {
        return labelService.update(labelDto, id);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final Long id) {
        labelService.deleteById(id);
    }
}
