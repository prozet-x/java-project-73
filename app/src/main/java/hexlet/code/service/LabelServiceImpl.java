package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@AllArgsConstructor
@Transactional
@Service
public class LabelServiceImpl implements LabelService {
    private LabelRepository labelRepository;
    private TaskRepository taskRepository;

    @Override
    public Label createNew(LabelDto labelDto) {
        Label label = new Label();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label update(LabelDto labelDto, Long id) {
        checkExistingById(id);
        Label label = labelRepository.findById(id).get();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public void deleteById(Long id) {
        checkExistingById(id);
        labelRepository.deleteById(id);
    }

    private void checkExistingById(final Long id) {
        if (!labelRepository.existsById(id)) {
            throw new NoSuchElementException(String.format("Label with id %d not found", id));
        }
        if (!taskRepository.existsByLabelsIsContaining(labelRepository.findById(id).get())) {
            labelRepository.deleteById(id);
        } else {
            throw new DataIntegrityViolationException("There are tasks in which this label is used");
        }
    }
}
