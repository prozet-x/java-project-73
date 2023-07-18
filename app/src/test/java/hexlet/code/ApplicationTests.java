package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDto;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Test
    public void testInit() {
        assertTrue(true);
    }
    @Test
    void testCreateUser() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        UserDto userDto = new UserDto("fn", "ln", "a@b.com", "pass");
        mockMvc.perform(post("/api/users").content(mapper.writeValueAsString(userDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
