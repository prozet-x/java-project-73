package hexlet.code;

import java.util.List;
import java.util.Map;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LoginDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import hexlet.code.component.JWTHelper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
class ApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static ObjectMapper mapper;

    @Autowired
    private JWTHelper jwtHelper;
    private static final String USERS_URL = "/users";
    private static final String ID_PATH_VAR = "/{id}";

    private static final String SPRING_USER_USERNAME = "username";

    private UserDto defaultUser1 = new UserDto("defFirstName1", "defLastName1", "def1@email.com", "defPassword1");
    private UserDto defaultUser2 = new UserDto("defFirstName2", "defLastName2", "def2@email.com", "defPassword2");

    private ResultActions addUser(UserDto userDto) throws Exception {
        String userDtoAsJSONString = mapper.writeValueAsString(userDto);

        MockHttpServletRequestBuilder creationReq = post(USERS_URL)
                .content(userDtoAsJSONString)
                .contentType(MediaType.APPLICATION_JSON);

        return performWithoutToken(creationReq);
    }

    @BeforeAll
    static void beforeAll() {
        mapper = new ObjectMapper();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void testInit() {
        assertTrue(true);
    }

    @Test
    void testCreateUserGood() throws Exception {
        assertEquals(userRepository.count(), 0);
        addUser(defaultUser1).andExpect(status().isCreated());
        assertEquals(userRepository.count(), 1);
    }

    @Test
    void testCreateUserBad() throws Exception {
        assertEquals(userRepository.count(), 0);

        UserDto userDtoBadEmail = new UserDto("fn", "ln", "badEmail", "pass");
        addUser(userDtoBadEmail).andExpect(status().isUnprocessableEntity());

        UserDto userDtoShortPassword = new UserDto("fn", "ln", "good@email.com", "pa");
        addUser(userDtoShortPassword).andExpect(status().isUnprocessableEntity());

        UserDto userDtoEmptyFirstName = new UserDto("", "ln", "good@email.com", "pass");
        addUser(userDtoEmptyFirstName).andExpect(status().isUnprocessableEntity());

        UserDto userDtoEmptyLastName = new UserDto("fn", "", "good@email.com", "pass");
        addUser(userDtoEmptyLastName).andExpect(status().isUnprocessableEntity());

        assertEquals(userRepository.count(), 0);
    }

    @Test
    void testDeleteUserGood() throws Exception {
        addUser(defaultUser1);
        Long id = userRepository.findByEmail(defaultUser1.getEmail()).get().getId();
        final MockHttpServletRequestBuilder req = delete(USERS_URL + "/" + id);
        performWithToken(req, defaultUser1.getEmail()).andExpect(status().isOk());
        assertEquals(userRepository.count(), 0);
    }

    @Test
    void testDeleteUserBad() throws Exception {
        addUser(defaultUser1);
        addUser(defaultUser2);
        Long id1 = userRepository.findByEmail(defaultUser1.getEmail()).get().getId();
        final MockHttpServletRequestBuilder req = delete(USERS_URL + ID_PATH_VAR, id1);
        performWithToken(req, defaultUser2.getEmail()).andExpect(status().isForbidden());
        assertEquals(userRepository.count(), 2);
    }

    @Test
    void testGetUserGood() throws Exception {
        addUser(defaultUser1);
        addUser(defaultUser2);

        User expectedUser = userRepository.findAll().get(0);
        User callingUser = userRepository.findAll().get(1);

        MockHttpServletRequestBuilder req =  get(USERS_URL + ID_PATH_VAR, expectedUser.getId());

        MockHttpServletResponse resp = performWithToken(req, callingUser.getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        User user = mapper.readValue(resp.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }

    @Test
    void testGetUserBad() throws Exception {
        addUser(defaultUser1);
        MockHttpServletRequestBuilder req = get(USERS_URL + ID_PATH_VAR, 100);
        performWithToken(req, defaultUser1.getEmail())
                .andExpect(status().isNotFound());
    }

    @Test
    void testGEtAllUsers() throws Exception {
        addUser(defaultUser1);
        addUser(defaultUser2);

        MockHttpServletRequestBuilder req = get(USERS_URL);

        MockHttpServletResponse resp = performWithoutToken(req)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<User> users = mapper.readValue(resp.getContentAsString(), new TypeReference<>(){});
        assertThat(users).hasSize(2);
    }

    @Test
    void testUpdateUser() throws Exception {
        addUser(defaultUser1);
        UserDto userDto = new UserDto("newFN", "newLN", "new@EMAIL.com", "newPWD");
        User oldUser = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder req = put(USERS_URL + ID_PATH_VAR, oldUser.getId())
                .content(mapper.writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON);
        performWithToken(req, oldUser.getEmail())
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(oldUser.getId()).get();

        assertEquals(updatedUser.getEmail(), userDto.getEmail());
        assertEquals(updatedUser.getFirstName(), userDto.getFirstName());
        assertEquals(updatedUser.getLastName(), userDto.getLastName());
    }

    private ResultActions performWithToken(MockHttpServletRequestBuilder req, String userName) throws Exception {
        String token = jwtHelper.expiring(Map.of(SPRING_USER_USERNAME, userName));
        req.header(HttpHeaders.AUTHORIZATION, token);
        return performWithoutToken(req);
    }

    private ResultActions performWithoutToken(MockHttpServletRequestBuilder req) throws Exception {
        return mockMvc.perform(req);
    }
}
