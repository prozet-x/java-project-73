package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    @NotBlank
    @Size(min = 1)
    private String firstName;

    @NotBlank
    @Size(min = 1)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 100)
    private String password;
}
