package hexlet.code.repository;

import hexlet.code.model.Label;
import hexlet.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
    boolean existsByAuthor(User user);
}
