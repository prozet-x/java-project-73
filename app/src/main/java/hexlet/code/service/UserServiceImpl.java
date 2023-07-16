package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public User createNew(User user) {
//        final User newUser = new User();
//        newUser.setEmail(userDto.getEmail());
//        newUser.setFirstName(userDto.getFirstName());
//        newUser.setLastName(userDto.getLastName());
//        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

//    @Override
//    public User createNew(UserDto userDto) {
//        final User newUser = new User();
//        newUser.setEmail(userDto.getEmail());
//        newUser.setFirstName(userDto.getFirstName());
//        newUser.setLastName(userDto.getLastName());
//        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        return userRepository.save(newUser);
//    }

    @Override
    public User update(final User userUpdated, final Long id) {
        final User user = userRepository.findById(id).get();
        user.setFirstName(userUpdated.getFirstName());
        user.setLastName(userUpdated.getLastName());
        user.setEmail(userUpdated.getEmail());
        user.setPassword(passwordEncoder.encode(userUpdated.getPassword()));
        return userRepository.save(user);
    }
}
