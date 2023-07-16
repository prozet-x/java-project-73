package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CREATED;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + "/users")
public class UserController {
    private static final String ID = "/{id}";
    private final UserRepository userRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    private static final String ONLY_OWNER_BY_ID = "@userRepository.findById(#id).get().getEmail() = authentication.getName()";

    @GetMapping
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping(ID)
    public UserDto getById(@PathVariable final Long id) {
        User user = userRepository.findById(id).get();
        return modelMapper.map(user, UserDto.class);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public UserDto registerNew(@RequestBody @Valid final User user) {
        return modelMapper.map(userService.createNew(user), UserDto.class);
    }

//    @PostMapping
//    @ResponseStatus(CREATED)
//    public User registerNew(@RequestBody @Valid final UserDto userDto) {
//        return userService.createNew(userDto);
//    }

    @PutMapping(ID)
    public UserDto update(@RequestBody @Valid User user, @PathVariable Long id) {
        return modelMapper.map(userService.update(user, id), UserDto.class);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final Long id) {
        userRepository.deleteById(id);
    }
}
