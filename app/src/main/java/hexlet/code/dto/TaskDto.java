package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDto {
    @NotBlank
    @Size(min = 1)
    private String name;

    private String description;

    @NotNull
    private Long taskStatusId;

    @JsonIgnore
    private Long authorId;

    private Long executorId;

    private List<Long> labelIds;

    public TaskDto(String name, String description, Long taskStatusId, Long executorId, List<Long> labels) {
        this.name = name;
        this.description = description;
        this.taskStatusId = taskStatusId;
        this.executorId = executorId;
        this.labelIds = List.copyOf(labels);
    }

    public TaskDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
