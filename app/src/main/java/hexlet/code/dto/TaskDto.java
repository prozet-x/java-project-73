package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDto {
    @NotBlank
    @Size(min = 1)
    private String name;

    private String descr;

    @NotNull
    private Long statusId;

    @NotNull
    private Long authorId;

    private Long executorId;

    private List<Long> labels;
}
