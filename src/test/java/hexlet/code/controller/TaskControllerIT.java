package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static hexlet.code.utils.TestUtils.DEFAULT_USER_1;
import static hexlet.code.utils.TestUtils.DEFAULT_USER_2;
import static hexlet.code.utils.TestUtils.DEFAULT_LABEL;
import static hexlet.code.utils.TestUtils.DEFAULT_TASK_STATUS_1;
import static hexlet.code.utils.TestUtils.ID_PATH_VAR;
import static hexlet.code.utils.TestUtils.toJSON;
import static hexlet.code.utils.TestUtils.fromJSON;
import static hexlet.code.utils.TestUtils.TASK_DEFAULT_NAME;
import static hexlet.code.utils.TestUtils.TASK_DEFAULT_DESC;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {
    @Autowired
    private TestUtils testUtils;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    void beforeEach() throws Exception {
        testUtils.addUser(DEFAULT_USER_1);
        testUtils.addTaskStatusUnderUser(DEFAULT_TASK_STATUS_1, DEFAULT_USER_1);
        testUtils.addLabelUnderUser(DEFAULT_LABEL, DEFAULT_USER_1);
    }
    @AfterEach
    void afterEach() {
        testUtils.clear();
    }
    @Test
    void testCreateTask() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        List<Long> labelsIds = List.of(labelRepository.findAll().get(0).getId());
        TaskDto taskDto = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId, labelsIds);

        assertEquals(taskRepository.count(), 0);

        testUtils.addTaskUnderUser(taskDto, DEFAULT_USER_1)
                .andExpect(status().isCreated());

        assertEquals(taskRepository.count(), 1);

        Task task = taskRepository.findAll().get(0);
        assertThat(task.getName()).isEqualTo(taskDto.getName());
        assertThat(task.getDescription()).isEqualTo(taskDto.getDescription());
        assertThat(task.getAuthor().getId()).isEqualTo(userId);
        assertThat(task.getExecutor().getId()).isEqualTo(taskDto.getExecutorId());
        assertThat(task.getTaskStatus().getId()).isEqualTo(taskDto.getTaskStatusId());
        assertThat(task.getLabels().get(0).getName()).isEqualTo(DEFAULT_LABEL.getName());

        TaskDto taskDtoBadName = new TaskDto("", "k", taskStatusId, userId, userId, labelsIds);
        testUtils.addTaskUnderUser(taskDtoBadName, DEFAULT_USER_1)
                .andExpect(status().isUnprocessableEntity());

        testUtils.addTaskUnauthorized(taskDtoBadName)
                .andExpect(status().isForbidden());
    }
    @Test
    void testGeTasksById() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        List<Long> labelsIds = List.of(labelRepository.findAll().get(0).getId());
        TaskDto taskDto = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId, labelsIds);
        testUtils.addTaskUnderUser(taskDto, DEFAULT_USER_1);
        Long id = taskRepository.findAll().get(0).getId();

        MockHttpServletRequestBuilder req = get(TASK_CONTROLLER_PATH + ID_PATH_VAR, id);
        String taskAsJSON = testUtils.getPerfomAuthorizedResultAsString(req, DEFAULT_USER_1);
        Task task = fromJSON(taskAsJSON, new TypeReference<Task>() { });
        assertThat(task.getName()).isEqualTo(TASK_DEFAULT_NAME);
        assertThat(task.getDescription()).isEqualTo(TASK_DEFAULT_DESC);

        String taskAsJSONUnauthorized = testUtils.getPerfomUnauthorizedResultAsString(req);
        Task taskUnauthorized = fromJSON(taskAsJSONUnauthorized, new TypeReference<Task>() { });
        assertThat(taskUnauthorized.getName()).isEqualTo(TASK_DEFAULT_NAME);
        assertThat(taskUnauthorized.getDescription()).isEqualTo(TASK_DEFAULT_DESC);
    }
    @Test
    void testGetAllTasks() throws Exception {
        testUtils.addUser(DEFAULT_USER_2);

        Long userId1 = userRepository.findAll().get(0).getId();
        Long userId2 = userRepository.findAll().get(1).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        List<Long> labelsIds = List.of(labelRepository.findAll().get(0).getId());

        TaskDto taskDto1 = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId2, labelsIds);
        TaskDto taskDto2 = new TaskDto("taskName2", "taskDesc2", taskStatusId, userId1, labelsIds);

        testUtils.addTaskUnderUser(taskDto1, DEFAULT_USER_1);
        testUtils.addTaskUnderUser(taskDto2, DEFAULT_USER_2);

        MockHttpServletRequestBuilder req = get(TASK_CONTROLLER_PATH);
        String tasksAsJSON = testUtils.getPerfomAuthorizedResultAsString(req, DEFAULT_USER_1);
        List<Task> tasks = fromJSON(tasksAsJSON, new TypeReference<List<Task>>() { });
        assertThat(tasks).hasSize(2);

        testUtils.getPerfomUnauthorizedResultAsString(req);
        List<Task> tasksUnauthorized = fromJSON(tasksAsJSON, new TypeReference<List<Task>>() { });
        assertThat(tasksUnauthorized).hasSize(2);
    }

    @Test
    void testFilteredGettingTasks() throws Exception {
        testUtils.addUser(DEFAULT_USER_2);
        testUtils.addLabelUnderUser(new LabelDto("label 2"), DEFAULT_USER_1);
        testUtils.addTaskStatusUnderUser(new TaskStatusDto("status 2"), DEFAULT_USER_1);

        List<User> users = userRepository.findAll();
        Long userId1 = users.get(0).getId();
        Long userId2 = users.get(1).getId();
        List<Label> labels = labelRepository.findAll();
        Long labelId1 = labels.get(0).getId();
        Long labelId2 = labels.get(1).getId();
        List<TaskStatus> statuses = taskStatusRepository.findAll();
        Long taskStatusId1 = statuses.get(0).getId();
        Long taskStatusId2 = statuses.get(1).getId();
        TaskDto taskDto1 = new TaskDto("taskName1", "taskDesc1", taskStatusId1, userId1, List.of(labelId1));
        TaskDto taskDto2 = new TaskDto("taskName2", "taskDesc2", taskStatusId2, userId2, List.of(labelId2));
        TaskDto taskDto3 = new TaskDto("taskName3", "taskDesc3", taskStatusId2, userId1, List.of(labelId1, labelId2));

        testUtils.addTaskUnderUser(taskDto1, DEFAULT_USER_1);
        testUtils.addTaskUnderUser(taskDto2, DEFAULT_USER_2);
        testUtils.addTaskUnderUser(taskDto3, DEFAULT_USER_2);
        assertEquals(taskRepository.count(), 3);

        MockHttpServletRequestBuilder req1 = get(TASK_CONTROLLER_PATH + "?executorId=" + userId1);
        String resAsString = testUtils.getPerfomAuthorizedResultAsString(req1, DEFAULT_USER_1);
        List<Task> tasks = fromJSON(resAsString, new TypeReference<List<Task>>() { });
        assertEquals(tasks.size(), 2);

        MockHttpServletRequestBuilder req2 = get(
                TASK_CONTROLLER_PATH
                + "?taskStatus="
                + taskStatusId2
                + "&labels="
                + labelId1
        );
        String resAsString2 = testUtils.getPerfomAuthorizedResultAsString(req2, DEFAULT_USER_1);
        List<Task> tasks2 = fromJSON(resAsString2, new TypeReference<List<Task>>() { });
        assertEquals(tasks2.size(), 1);
        assertEquals(tasks2.get(0).getName(), "taskName3");

        MockHttpServletRequestBuilder req3 = get(
                TASK_CONTROLLER_PATH
                        + "?authorId="
                        + (userId1 + userId2 + 1));
        String resAsString3 = testUtils.getPerfomAuthorizedResultAsString(req3, DEFAULT_USER_1);
        List<Task> tasks3 = fromJSON(resAsString3, new TypeReference<List<Task>>() { });
        assertEquals(tasks3.size(), 0);
    }

    @Test
    void testUpdateTask() throws Exception {
        testUtils.addUser(DEFAULT_USER_2);

        Long userId1 = userRepository.findAll().get(0).getId();
        Long userId2 = userRepository.findAll().get(1).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        List<Long> labelsIds = List.of(labelRepository.findAll().get(0).getId());
        TaskDto taskDto = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId1, labelsIds);

        testUtils.addTaskUnderUser(taskDto, DEFAULT_USER_1);
        Task task = taskRepository.findAll().get(0);
        Long id = task.getId();
        Long authorId = task.getAuthor().getId();
        TaskDto taskDtoForUpdate = new TaskDto("taskNameUpdated", "taskDescUpdated", taskStatusId, userId2, labelsIds);

        MockHttpServletRequestBuilder req = put(TASK_CONTROLLER_PATH + ID_PATH_VAR, id)
                .content(toJSON(taskDtoForUpdate))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithToken(req, DEFAULT_USER_1)
                .andExpect(status().isOk());

        Task updatedTask = taskRepository.findAll().get(0);
        assertThat(updatedTask.getName()).isEqualTo(taskDtoForUpdate.getName());
        assertThat(updatedTask.getDescription()).isEqualTo(taskDtoForUpdate.getDescription());
        assertThat(updatedTask.getAuthor().getId()).isEqualTo(authorId);
        assertThat(updatedTask.getExecutor().getId()).isEqualTo(userId2);

        MockHttpServletRequestBuilder reqUnauthorized = put(TASK_CONTROLLER_PATH + ID_PATH_VAR, id)
                .content(toJSON(taskDtoForUpdate))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithoutToken(reqUnauthorized)
                .andExpect(status().isForbidden());
    }
    @Test
    void testDeleteTask() throws Exception {
        assertThat(taskRepository.count()).isEqualTo(0);

        Long userId = userRepository.findAll().get(0).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        List<Long> labelsIds = List.of(labelRepository.findAll().get(0).getId());
        TaskDto taskDto = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId, labelsIds);

        testUtils.addTaskUnderUser(taskDto, DEFAULT_USER_1);
        assertThat(taskRepository.count()).isEqualTo(1);

        Long id = taskRepository.findAll().get(0).getId();

        MockHttpServletRequestBuilder req = delete(TASK_CONTROLLER_PATH + ID_PATH_VAR, id);
        testUtils.performWithoutToken(req)
                .andExpect(status().isForbidden());
        assertThat(taskRepository.count()).isEqualTo(1);

        MockHttpServletRequestBuilder reqForDeletingUnderOtherUser = delete(TASK_CONTROLLER_PATH + ID_PATH_VAR, id);
        testUtils.performWithToken(reqForDeletingUnderOtherUser, DEFAULT_USER_2)
                .andExpect(status().isForbidden());
        assertThat(taskRepository.count()).isEqualTo(1);

        MockHttpServletRequestBuilder reqForDeletingUser = delete(USER_CONTROLLER_PATH + ID_PATH_VAR, userId);
        testUtils.performWithToken(reqForDeletingUser, DEFAULT_USER_1)
                        .andExpect(status().isUnprocessableEntity());

        MockHttpServletRequestBuilder reqForDeletingTaskStatus = delete(
                STATUS_CONTROLLER_PATH + ID_PATH_VAR,
                taskStatusId
        );
        testUtils.performWithToken(reqForDeletingTaskStatus, DEFAULT_USER_1)
                .andExpect(status().isUnprocessableEntity());

        testUtils.performWithToken(req, DEFAULT_USER_1)
                .andExpect(status().isOk());

        assertThat(taskRepository.count()).isEqualTo(0);
    }
}
