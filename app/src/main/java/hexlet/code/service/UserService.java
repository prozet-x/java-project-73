package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

public interface UserService {
    User createNew(User user);
    User update(User user, Long id);
}
