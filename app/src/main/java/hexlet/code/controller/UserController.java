package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private static final String ONLY_OWNER_BY_ID =
            "@userRepository.findById(#id).get().getEmail() == authentication.getName()";

    @Operation(summary = "Get all users")
    @ApiResponse(
            responseCode = "200",
            description = "All users got",
            content = {
                @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))
            }
    )
    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
        @ApiResponse(
                    responseCode = "200",
                    description = "User got",
                    content = {
                        @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    }
            ),
        @ApiResponse(responseCode = "404", description = "User with given id not found")
    })
    @GetMapping(ID)
    public User getById(@PathVariable @Parameter(description = "Id of user to get") final Long id) {
        return userRepository.findById(id).get();
    }

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
        @ApiResponse(
                    responseCode = "201",
                    description = "New user created",
                    content = {
                        @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    }
            ),
        @ApiResponse(responseCode = "422", description = "Bad input data")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public User registerNew(@RequestBody @Valid @Parameter(description = "User to create") final UserDto userDto) {
        return userService.createNew(userDto);
    }

    @Operation(summary = "Update user by id")
    @ApiResponses(value = {
        @ApiResponse(
                    responseCode = "200",
                    description = "User updated",
                    content = {
                        @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    }
            ),
        @ApiResponse(responseCode = "404", description = "User with given id not found"),
        @ApiResponse(responseCode = "422", description = "Bad input data")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @PutMapping(ID)
    public User update(
            @RequestBody @Valid @Parameter(description = "New user data") UserDto userDto,
            @PathVariable @Parameter(description = "Id of user to update") final Long id) {
        return userService.update(userDto, id);
    }

    @Operation(summary = "Delete user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted"),
        @ApiResponse(responseCode = "404", description = "User with given id not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void delete(@PathVariable @Parameter(description = "Id of user to delete") final long id) {
        userService.deleteById(id);
    }
}
