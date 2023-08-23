package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static hexlet.code.utils.TestUtils.*;
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
        testUtils.addUser(defaultUser1);
        testUtils.addTaskStatusUnderUser(defaultTaskStatus1, defaultUser1);
        testUtils.addLabelUnderUser(defaultLabel, defaultUser1);
    }

    @AfterEach
    void clearBase() {
        testUtils.clear();
    }

    @Test
    void testCreateTask() throws Exception {
        //Во многих тестах подготовительный этап одинаков. Вынести его в отдельный метод
        Long userId = userRepository.findAll().get(0).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        List<Long> labelsIds = List.of(labelRepository.findAll().get(0).getId());
        TaskDto taskDto = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId, userId, labelsIds);

        assertEquals(taskRepository.count(), 0);

        testUtils.addTaskUnderUser(taskDto, defaultUser1)
                .andExpect(status().isOk());

        assertEquals(taskRepository.count(), 1);

        Task task = taskRepository.findAll().get(0);
        assertThat(task.getName()).isEqualTo(taskDto.getName());
        assertThat(task.getDescr()).isEqualTo(taskDto.getDescr());
        assertThat(task.getAuthor().getId()).isEqualTo(taskDto.getAuthorId());
        assertThat(task.getExecutor().getId()).isEqualTo(taskDto.getExecutorId());
        assertThat(task.getStatus().getId()).isEqualTo(taskDto.getStatusId());
        assertThat(task.getLabels().get(0).getName()).isEqualTo(taskDto.getLabels().get(0));

        TaskDto taskDtoBadName = new TaskDto("", "k", taskStatusId, userId, userId, labelsIds);
        testUtils.addTaskUnderUser(taskDtoBadName, defaultUser1)
                .andExpect(status().isUnprocessableEntity());

        testUtils.addTaskUnauthorized(taskDtoBadName)
                .andExpect(status().isForbidden());
    }

    @Test
    void testGeTasksById() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        List<Long> labelsIds = List.of(labelRepository.findAll().get(0).getId());
        TaskDto taskDto = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId, userId, labelsIds);
        testUtils.addTaskUnderUser(taskDto, defaultUser1);
        Long id = taskRepository.findAll().get(0).getId();

        MockHttpServletRequestBuilder req = get(TASK_CONTROLLER_PATH + ID_PATH_VAR, id);
        String taskAsJSON = testUtils.performWithToken(req, defaultUser1)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Task task = fromJSON(taskAsJSON, new TypeReference<Task>() {});
        assertThat(task.getName()).isEqualTo(TASK_DEFAULT_NAME);
        assertThat(task.getDescr()).isEqualTo(TASK_DEFAULT_DESC);

        String taskAsJSONUnauthorized = testUtils.performWithoutToken(req)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Task taskUnauthorized = fromJSON(taskAsJSONUnauthorized, new TypeReference<Task>() {});
        assertThat(taskUnauthorized.getName()).isEqualTo(TASK_DEFAULT_NAME);
        assertThat(taskUnauthorized.getDescr()).isEqualTo(TASK_DEFAULT_DESC);
    }

    @Test
    void testGetAllTasks() throws Exception {
        testUtils.addUser(defaultUser2);

        Long userId1 = userRepository.findAll().get(0).getId();
        Long userId2 = userRepository.findAll().get(1).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        List<Long> labelsIds = List.of(labelRepository.findAll().get(0).getId());

        TaskDto taskDto1 = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId1, userId2, labelsIds);
        TaskDto taskDto2 = new TaskDto("taskName2", "taskDesc2", taskStatusId, userId2, userId1, labelsIds);

        testUtils.addTaskUnderUser(taskDto1, defaultUser1);
        testUtils.addTaskUnderUser(taskDto2, defaultUser2);

        MockHttpServletRequestBuilder req = get(TASK_CONTROLLER_PATH);
        String tasksAsJSON = testUtils.performWithToken(req, defaultUser1)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Task> tasks = fromJSON(tasksAsJSON, new TypeReference<List<Task>>(){});
        assertThat(tasks).hasSize(2);

        String tasksAsJSONUnauthorized = testUtils.performWithoutToken(req)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Task> tasksUnauthorized = fromJSON(tasksAsJSON, new TypeReference<List<Task>>(){});
        assertThat(tasksUnauthorized).hasSize(2);
    }

    @Test
    void testUpdateTask() throws Exception {
        testUtils.addUser(defaultUser2);

        Long userId1 = userRepository.findAll().get(0).getId();
        Long userId2 = userRepository.findAll().get(1).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        List<Long> labelsIds = List.of(labelRepository.findAll().get(0).getId());
        TaskDto taskDto = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId1, userId1, labelsIds);

        testUtils.addTaskUnderUser(taskDto, defaultUser1);
        Long id = taskRepository.findAll().get(0).getId();
        TaskDto taskDtoForUpdate = new TaskDto("taskNameUpdated", "taskDescUpdated", taskStatusId, userId2, userId2, labelsIds);

        MockHttpServletRequestBuilder req = put(TASK_CONTROLLER_PATH + ID_PATH_VAR, id)
                .content(toJSON(taskDtoForUpdate))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithToken(req, defaultUser1)
                .andExpect(status().isOk());

        Task updatedTask = taskRepository.findAll().get(0);
        assertThat(updatedTask.getName()).isEqualTo(taskDtoForUpdate.getName());
        assertThat(updatedTask.getDescr()).isEqualTo(taskDtoForUpdate.getDescr());
        assertThat(updatedTask.getAuthor().getId()).isEqualTo(userId2);
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
        TaskDto taskDto = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId, userId, labelsIds);

        testUtils.addTaskUnderUser(taskDto, defaultUser1);
        assertThat(taskRepository.count()).isEqualTo(1);

        Long id = taskRepository.findAll().get(0).getId();

        MockHttpServletRequestBuilder req = delete(TASK_CONTROLLER_PATH + ID_PATH_VAR, id);
        testUtils.performWithoutToken(req)
                .andExpect(status().isForbidden());
        assertThat(taskRepository.count()).isEqualTo(1);

        MockHttpServletRequestBuilder reqForDeletingUnderOtherUser = delete(TASK_CONTROLLER_PATH + ID_PATH_VAR, id);
        testUtils.performWithToken(reqForDeletingUnderOtherUser, defaultUser2)
                .andExpect(status().isForbidden());
        assertThat(taskRepository.count()).isEqualTo(1);

        MockHttpServletRequestBuilder reqForDeletingUser = delete(USER_CONTROLLER_PATH + ID_PATH_VAR, userId);
        testUtils.performWithToken(reqForDeletingUser, defaultUser1)
                        .andExpect(status().isUnprocessableEntity());

        MockHttpServletRequestBuilder reqForDeletingTaskStatus = delete(STATUS_CONTROLLER_PATH + ID_PATH_VAR, taskStatusId);
        testUtils.performWithToken(reqForDeletingTaskStatus, defaultUser1)
                .andExpect(status().isUnprocessableEntity());

        testUtils.performWithToken(req, defaultUser1)
                .andExpect(status().isOk());

        assertThat(taskRepository.count()).isEqualTo(0);
    }
}
