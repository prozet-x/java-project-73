package hexlet.code;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static ObjectMapper mapper;

    private static final String USERS_URL = "/api/users";
    private static final String ID_PATH_VAR = "/{id}";

    private UserDto defaultUser = new UserDto("defFirstName", "defLastName", "def@email.com", "defPassword");

    private ResultActions addUser(UserDto userDto) throws Exception {
        MockHttpServletRequestBuilder creationReq = post(USERS_URL).content(mapper.writeValueAsString(userDto)).contentType(MediaType.APPLICATION_JSON);
        return mockMvc.perform(creationReq);
    }

    private ResultActions addDefaultUser() throws Exception {
        return addUser(defaultUser);
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
        addDefaultUser().andExpect(status().isCreated());
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
        addDefaultUser();
        Long id = userRepository.findByEmail("def@email.com").get().getId();
        mockMvc.perform(delete(USERS_URL + "/" + id)).andExpect(status().isOk());
        assertEquals(userRepository.count(), 0);
    }

    @Disabled
    @Test
    void testDeleteUserBad() throws Exception {
        mockMvc.perform(delete(USERS_URL + "/999")).andExpect(status().isNotFound());
        assertEquals(userRepository.count(), 0);
    }

    @Disabled
    @Test
    void testGetUserGood() throws Exception {
        addDefaultUser();
        addUser(new UserDto("fn", "ln", "e@mail.com", "pwd"));

        User expectedUser = userRepository.findAll().get(1);

        MockHttpServletResponse response = mockMvc.perform(get(USERS_URL + ID_PATH_VAR, expectedUser.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        User user = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }

    @Disabled
    @Test
    void testGetUserBad() throws Exception {
        addDefaultUser();
        mockMvc.perform(get(USERS_URL + "/0"))
                .andExpect(status().isNotFound());
    }

    @Disabled
    @Test
    void testGEtAllUsers() throws Exception {
        addDefaultUser();
        addUser(new UserDto("fn", "ln", "e@mail.com", "pwd"));

        MockHttpServletResponse resp = mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<User> users = mapper.readValue(resp.getContentAsString(), new TypeReference<>(){});
        assertThat(users).hasSize(2);
    }

    @Disabled
    @Test
    void testUpdateUser() throws Exception {
        addDefaultUser();
        UserDto userDto = new UserDto("newFN", "newLN", "new@EMAIL.com", "newPWD");
        User oldUser = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder creationReq = put(USERS_URL + ID_PATH_VAR, oldUser.getId())
                .content(mapper.writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(creationReq)
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(oldUser.getId()).get();

        assertEquals(updatedUser.getEmail(), userDto.getEmail());
        assertEquals(updatedUser.getFirstName(), userDto.getFirstName());
        assertEquals(updatedUser.getLastName(), userDto.getLastName());
    }
}
