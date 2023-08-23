package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import org.springframework.stereotype.Service;

public interface LabelService {
    Label createNew(LabelDto labelDto);
    Label update(LabelDto labelDto, Long id);
    void deleteById(Long id);
}
