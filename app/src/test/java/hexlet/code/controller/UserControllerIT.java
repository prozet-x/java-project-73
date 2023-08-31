package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.util.List;
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerIT {
    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clearBase() {
        testUtils.clear();
    }

    @Test
    void testCreateUserGood() throws Exception {
        assertEquals(userRepository.count(), 0);
        testUtils.addUser(defaultUser1).andExpect(status().isCreated());
        assertEquals(userRepository.count(), 1);
    }

    @Test
    void testCreateUserBad() throws Exception {
        assertEquals(userRepository.count(), 0);

        UserDto userDtoBadEmail = new UserDto("fn", "ln", "badEmail", "pass");
        testUtils.addUser(userDtoBadEmail).andExpect(status().isUnprocessableEntity());

        UserDto userDtoShortPassword = new UserDto("fn", "ln", "good@email.com", "pa");
        testUtils.addUser(userDtoShortPassword).andExpect(status().isUnprocessableEntity());

        UserDto userDtoEmptyFirstName = new UserDto("", "ln", "good@email.com", "pass");
        testUtils.addUser(userDtoEmptyFirstName).andExpect(status().isUnprocessableEntity());

        UserDto userDtoEmptyLastName = new UserDto("fn", "", "good@email.com", "pass");
        testUtils.addUser(userDtoEmptyLastName).andExpect(status().isUnprocessableEntity());

        assertEquals(userRepository.count(), 0);
    }

    @Test
    void testDeleteUserGood() throws Exception {
        testUtils.addUser(defaultUser1);
        Long id = userRepository.findByEmail(defaultUser1.getEmail()).get().getId();
        final MockHttpServletRequestBuilder req = delete(USER_CONTROLLER_PATH + "/" + id);
        testUtils.performWithToken(req, defaultUser1).andExpect(status().isOk());
        assertEquals(userRepository.count(), 0);
    }

    @Test
    void testDeleteUserBad() throws Exception {
        testUtils.addUser(defaultUser1);
        testUtils.addUser(defaultUser2);
        Long id1 = userRepository.findByEmail(defaultUser1.getEmail()).get().getId();
        final MockHttpServletRequestBuilder req = delete(USER_CONTROLLER_PATH + ID_PATH_VAR, id1);
        testUtils.performWithToken(req, defaultUser2).andExpect(status().isForbidden());
        assertEquals(userRepository.count(), 2);
    }

    @Test
    void testGetUserGood() throws Exception {
        testUtils.addUser(defaultUser1);
        testUtils.addUser(defaultUser2);
        Long id = userRepository.findByEmail(defaultUser1.getEmail()).get().getId();

        MockHttpServletRequestBuilder req =  get(USER_CONTROLLER_PATH + ID_PATH_VAR, id);
        String resultAsJSON = testUtils.getPerfomAuthorizedResultAsString(req, defaultUser2);
        User user = fromJSON(resultAsJSON, new TypeReference<>() {
        });

        assertEquals(defaultUser1.getEmail(), user.getEmail());
        assertEquals(defaultUser1.getFirstName(), user.getFirstName());
        assertEquals(defaultUser1.getLastName(), user.getLastName());
    }

    @Test
    void testGetUserBad() throws Exception {
        testUtils.addUser(defaultUser1);
        MockHttpServletRequestBuilder req = get(USER_CONTROLLER_PATH + ID_PATH_VAR, 100);
        testUtils.performWithToken(req, defaultUser1)
                .andExpect(status().isNotFound());
    }

    @Test
    void testGEtAllUsers() throws Exception {
        testUtils.addUser(defaultUser1);
        testUtils.addUser(defaultUser2);

        MockHttpServletRequestBuilder req = get(USER_CONTROLLER_PATH);
        String resultAsJSON = testUtils.getPerfomUnauthorizedResultAsString(req);
        List<User> users = fromJSON(resultAsJSON, new TypeReference<>(){});
        assertThat(users).hasSize(2);
    }

    @Test
    void testUpdateUser() throws Exception {
        testUtils.addUser(defaultUser1);
        UserDto userDto = new UserDto("newFN", "newLN", "new@EMAIL.com", "newPWD");
        Long id = userRepository.findAll().get(0).getId();
        // User oldUser = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder req = put(USER_CONTROLLER_PATH + ID_PATH_VAR, id)
                .content(toJSON(userDto))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithToken(req, defaultUser1)
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(id).get();

        assertEquals(updatedUser.getEmail(), userDto.getEmail());
        assertEquals(updatedUser.getFirstName(), userDto.getFirstName());
        assertEquals(updatedUser.getLastName(), userDto.getLastName());
    }
}
