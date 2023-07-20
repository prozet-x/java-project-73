package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.TypeRef;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.util.List;
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

    private UserDto defaultUser = new UserDto("defFirstName", "defLastName", "def@email.com", "defPassword");

    private ResultActions addUser(UserDto userDto) throws Exception {
        MockHttpServletRequestBuilder creationReq = post("/api/users").content(mapper.writeValueAsString(userDto)).contentType(MediaType.APPLICATION_JSON);
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
        Long id = userRepository.findByEmail("def@email.com").getId();
        mockMvc.perform(delete(String.format("/api/users/%d", id))).andExpect(status().isOk());
        assertEquals(userRepository.count(), 0);
    }

    @Test
    void testDeleteUserBad() throws Exception {
        mockMvc.perform(delete("/api/users/1")).andExpect(status().isNotFound());
        assertEquals(userRepository.count(), 0);
    }

    @Test
    void testGetUserGood() throws Exception {
        addDefaultUser();
        addUser(new UserDto("fn", "ln", "e@mail.com", "pwd"));

        User expectedUser = userRepository.findAll().get(1);

        MockHttpServletResponse response = mockMvc.perform(get("/api/users/{id}", expectedUser.getId()))
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

    @Test
    void testGetUserBad() throws Exception {
        addDefaultUser();
        mockMvc.perform(get("/api/users/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGEtAllUsers() throws Exception {
        addDefaultUser();
        addUser(new UserDto("fn", "ln", "e@mail.com", "pwd"));

        MockHttpServletResponse resp = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<User> users = mapper.readValue(resp.getContentAsString(), new TypeReference<>(){});
        assertThat(users).hasSize(2);
    }

    @Test
    void testUpdateUser() throws Exception {
        addDefaultUser();
        UserDto userDto = new UserDto("newFN", "newLN", "new@EMAIL.com", "newPWD");
        User oldUser = userRepository.findAll().get(0);

        MockHttpServletRequestBuilder creationReq = put("/api/users/{id}", oldUser.getId())
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
