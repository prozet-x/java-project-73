package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public User createNew(UserDto userDto) {
        final User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(newUser);
    }

    @Override
    public User update(final UserDto userUpdated, final Long id) {
        final User user = userRepository.findById(id).get();
        user.setFirstName(userUpdated.getFirstName());
        user.setLastName(userUpdated.getLastName());
        user.setEmail(userUpdated.getEmail());
        user.setPassword(passwordEncoder.encode(userUpdated.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException(String.format("User with id %d not found", id));
        }
        if (!taskRepository.existsByAuthor(userRepository.findById(id).get())) {
            userRepository.deleteById(id);
        } else {
            throw new DataIntegrityViolationException("There are tasks where the author is the user being deleted");
        }
    }
}
