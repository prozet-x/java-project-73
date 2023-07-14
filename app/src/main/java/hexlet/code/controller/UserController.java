package hexlet.code.controller;

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
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/users")
public class UserController {
    private static final String ID = "/{id}";
    private final UserRepository userRepository;
    private final UserService userService;

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

    @DeleteMapping(ID)
    public
}
