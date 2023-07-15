package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

public interface UserService {
    User createNew(UserDto userDto);
    User update(UserDto userDto, Long id);
}
