package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDto {
    @NotBlank
    private String name;

    private String descr;

    private Long statusId;

    private Long authorId;

    private Long executorId;
}
