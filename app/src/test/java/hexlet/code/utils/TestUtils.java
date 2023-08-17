package hexlet.code.utils;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import hexlet.code.component.JWTHelper;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    public static final String ID_PATH_VAR = "/{id}";
    public static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JWTHelper jwtHelper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    public static final UserDto defaultUser1 = new UserDto("defFirstName1", "defLastName1", "def1@email.com", "defPassword1");
    public static final UserDto defaultUser2 = new UserDto("defFirstName2", "defLastName2", "def2@email.com", "defPassword2");
    public static final TaskStatusDto defaultTaskStatus1 = new TaskStatusDto("status1");
    public static final TaskStatusDto defaultTaskStatus2 = new TaskStatusDto("status2");

    public static final String SPRING_USER_USERNAME = "username";

    public ResultActions addUser(UserDto userDto) throws Exception {
        String userDtoAsJSONString = toJSON(userDto);

        MockHttpServletRequestBuilder creationReq = post(USER_CONTROLLER_PATH)
                .content(userDtoAsJSONString)
                .contentType(MediaType.APPLICATION_JSON);

        return performWithoutToken(creationReq);
    }

    public void clear() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    public ResultActions performWithToken(MockHttpServletRequestBuilder req, String userName) throws Exception {
        String token = jwtHelper.expiring(Map.of(SPRING_USER_USERNAME, userName));
        req.header(HttpHeaders.AUTHORIZATION, token);
        return performWithoutToken(req);
    }

    public ResultActions performWithoutToken(MockHttpServletRequestBuilder req) throws Exception {
        return mockMvc.perform(req);
    }

    public static String toJSON(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public static <T> T fromJSON(String from, TypeReference<T> to) throws JsonProcessingException {
        return mapper.readValue(from, to);
    }
}
