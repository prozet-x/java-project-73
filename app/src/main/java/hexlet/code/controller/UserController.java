package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CREATED;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + "/users")
public class UserController {
    public static final String ID = "/{id}";
    private final UserRepository userRepository;
    private final UserService userService;

    private static final String ONLY_OWNER_BY_ID = "@userRepository.findById(#id).get().getEmail() = authentication.getName()";

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping(ID)
    public User getById(@PathVariable final Long id) {
        return userRepository.findById(id).get();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public User registerNew(@RequestBody @Valid final UserDto userDto) {
        return userService.createNew(userDto);
    }

    @PutMapping(ID)
    public User update(@RequestBody @Valid UserDto userDto, @PathVariable Long id) {
        return userService.update(userDto, id);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final Long id) {
        userService.deleteById(id);
    }
}
