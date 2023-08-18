package hexlet.code.utils;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
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

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
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
    private TaskRepository taskRepository;

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

    public ResultActions addTaskStatusUnderUser(TaskStatusDto taskStatusDto, String userName) throws Exception {
        String taskStatusDtoAsJSONString = mapper.writeValueAsString(taskStatusDto);

        MockHttpServletRequestBuilder creationReq = post(STATUS_CONTROLLER_PATH)
                .content(taskStatusDtoAsJSONString)
                .contentType(MediaType.APPLICATION_JSON);

        return performWithToken(creationReq, userName);
    }

    public ResultActions addTaskUnderUser(TaskDto taskDto, UserDto userDto) throws Exception {
        MockHttpServletRequestBuilder req = post(TASK_CONTROLLER_PATH)
                .content(toJSON(taskDto))
                .contentType(MediaType.APPLICATION_JSON);
        return performWithToken(req, userDto.getEmail());
    }

    public TaskDto fillTaskDto(TaskDto taskDto, Long statusId, Long authorId, Long executorId) {
        taskDto.setStatusId(statusId);;
        taskDto.setExecutorId(executorId);
        taskDto.setAuthorId(authorId);
        return taskDto;
    }

    public TaskDto getDefaultTaskDto() {
        TaskDto taskDto = new TaskDto();
        taskDto.setName("defTaskName");
        taskDto.setDescr("defTaskDescr");
        return taskDto;
    }

    public void clear() {
        taskRepository.deleteAll();
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
