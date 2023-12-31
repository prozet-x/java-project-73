package hexlet.code.utils;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TestUtils {
    public static final String ID_PATH_VAR = "/{id}";
    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    @Autowired
    private LabelRepository labelRepository;

    public static final UserDto DEFAULT_USER_1 = new UserDto(
            "defFirstName1",
            "defLastName1",
            "def1@email.com",
            "defPassword1");
    public static final UserDto DEFAULT_USER_2 = new UserDto(
            "defFirstName2",
            "defLastName2",
            "def2@email.com",
            "defPassword2");
    public static final TaskStatusDto DEFAULT_TASK_STATUS_1 = new TaskStatusDto("status1");
    public static final TaskStatusDto DEFAULT_TASK_STATUS_2 = new TaskStatusDto("status2");
    public static final LabelDto DEFAULT_LABEL = new LabelDto("defLabel");
    public static final String TASK_DEFAULT_NAME = "defTaskName";
    public static final String TASK_DEFAULT_DESC = "defTaskDesc";
    private static TaskDto defaultTaskDto = new TaskDto(TASK_DEFAULT_NAME, TASK_DEFAULT_DESC);
    public static final String SPRING_USER_USERNAME = "username";

    public TaskDto getDefaultTaskDto() {
        return defaultTaskDto;
    }

    public ResultActions addUser(UserDto userDto) throws Exception {
        String userDtoAsJSONString = toJSON(userDto);

        MockHttpServletRequestBuilder creationReq = post(USER_CONTROLLER_PATH)
                .content(userDtoAsJSONString)
                .contentType(MediaType.APPLICATION_JSON);

        return performWithoutToken(creationReq);
    }

    public void addLabelUnderUser(LabelDto labelDto, UserDto userDto) throws Exception {
        String labelAsJSON = toJSON(labelDto);

        MockHttpServletRequestBuilder creationReq = post(LABEL_CONTROLLER_PATH)
                .content(labelAsJSON)
                .contentType(MediaType.APPLICATION_JSON);

        performWithToken(creationReq, userDto);
    }

    public void addTaskStatusUnderUser(TaskStatusDto taskStatusDto, UserDto userDto) throws Exception {
        String taskStatusDtoAsJSONString = toJSON(taskStatusDto);

        MockHttpServletRequestBuilder creationReq = post(STATUS_CONTROLLER_PATH)
                .content(taskStatusDtoAsJSONString)
                .contentType(MediaType.APPLICATION_JSON);

        performWithToken(creationReq, userDto);
    }

    public ResultActions addTaskUnderUser(TaskDto taskDto, UserDto userDto) throws Exception {
        MockHttpServletRequestBuilder req = post(TASK_CONTROLLER_PATH)
                .content(toJSON(taskDto))
                .contentType(MediaType.APPLICATION_JSON);
        return performWithToken(req, userDto);
    }

    public ResultActions addTaskUnauthorized(TaskDto taskDto) throws Exception {
        MockHttpServletRequestBuilder req = post(TASK_CONTROLLER_PATH)
                .content(toJSON(taskDto))
                .contentType(MediaType.APPLICATION_JSON);
        return performWithoutToken(req);
    }

    public TaskDto fillTaskDto(TaskDto taskDto, Long statusId, Long executorId, List<Long> labels) {
        taskDto.setTaskStatusId(statusId);
        taskDto.setExecutorId(executorId);
        taskDto.setLabelIds(List.copyOf(labels));
        return taskDto;
    }

    public void clear() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    public ResultActions performWithToken(MockHttpServletRequestBuilder req, UserDto userDto) throws Exception {
        String token = jwtHelper.expiring(Map.of(SPRING_USER_USERNAME, userDto.getEmail()));
        req.header(HttpHeaders.AUTHORIZATION, token);
        return performWithoutToken(req);
    }

    public ResultActions performWithoutToken(MockHttpServletRequestBuilder req) throws Exception {
        return mockMvc.perform(req);
    }

    public static String toJSON(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    public static <T> T fromJSON(String from, TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(from, to);
    }

    public String getPerfomAuthorizedResultAsString(
            MockHttpServletRequestBuilder req,
            UserDto userDto) throws Exception {
        return performWithToken(req, userDto)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    public String getPerfomUnauthorizedResultAsString(MockHttpServletRequestBuilder req) throws Exception {
        return performWithoutToken(req)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
