package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDto;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
