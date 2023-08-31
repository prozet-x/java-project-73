package hexlet.code.repository;

import com.querydsl.core.types.dsl.StringPath;
import hexlet.code.model.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface TaskRepository extends JpaRepository<Task, Long>, QuerydslPredicateExecutor<Task>, QuerydslBinderCustomizer<QTask> {
    boolean existsByAuthor(User user);
    boolean existsByTaskStatus(TaskStatus status);
    boolean existsByLabelsIsContaining(Label label);

    @Override
    default void customize(QuerydslBindings bindings, QTask root) {
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
    }
}
