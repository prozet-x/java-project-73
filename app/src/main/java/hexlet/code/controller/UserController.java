package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CREATED;
import java.util.List;
import lombok.AllArgsConstructor;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    private static final String ID = "/{id}";
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

    @PreAuthorize(ONLY_OWNER_BY_ID)
    @PutMapping(ID)
    public User update(@RequestBody @Valid UserDto userDto, @PathVariable final Long id) {
        return userService.update(userDto, id);
    }

    @PreAuthorize(ONLY_OWNER_BY_ID)
    @DeleteMapping(ID)
    public void delete(@PathVariable final Long id) {
        userService.deleteById(id);
    }
}
