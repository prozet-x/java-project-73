package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.toJSON;
import static hexlet.code.utils.TestUtils.fromJSON;
import static hexlet.code.utils.TestUtils.DEFAULT_USER_1;
import static hexlet.code.utils.TestUtils.DEFAULT_TASK_STATUS_1;
import static hexlet.code.utils.TestUtils.ID_PATH_VAR;
import static hexlet.code.utils.TestUtils.DEFAULT_TASK_STATUS_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskStatusControllerIT {
    @Autowired
    private TestUtils testUtils;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @BeforeEach
    void beforeEach() throws Exception {
        testUtils.addUser(DEFAULT_USER_1);
    }

    @AfterEach
    void clearBase() {
        testUtils.clear();
    }

    @Test
    void testTaskStatusCreate() throws Exception {
        assertEquals(taskStatusRepository.count(), 0);

        MockHttpServletRequestBuilder req = post(STATUS_CONTROLLER_PATH)
                .content(toJSON(DEFAULT_TASK_STATUS_1))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.performWithoutToken(req).andExpect(status().isForbidden());
        assertEquals(taskStatusRepository.count(), 0);

        testUtils.performWithToken(req, DEFAULT_USER_1).andExpect(status().isCreated());
        assertEquals(taskStatusRepository.count(), 1);

        String nameOfSavedStatus = taskStatusRepository.findAll().get(0).getName();
        assertEquals(nameOfSavedStatus, DEFAULT_TASK_STATUS_1.getName());
    }

    @Test
    void testTaskStatusDelete() throws Exception {
        testUtils.addTaskStatusUnderUser(DEFAULT_TASK_STATUS_1, DEFAULT_USER_1);
        assertEquals(taskStatusRepository.count(), 1);

        Long id = taskStatusRepository.findAll().get(0).getId();
        MockHttpServletRequestBuilder reqDel = delete(STATUS_CONTROLLER_PATH + ID_PATH_VAR, id);

        testUtils.performWithoutToken(reqDel).andExpect(status().isForbidden());
        assertEquals(taskStatusRepository.count(), 1);

        testUtils.performWithToken(reqDel, DEFAULT_USER_1).andExpect(status().isOk());
        assertEquals(taskStatusRepository.count(), 0);
    }

    @Test
    void testTaskStatusGet() throws Exception {
        testUtils.addTaskStatusUnderUser(DEFAULT_TASK_STATUS_1, DEFAULT_USER_1);

        Long id = taskStatusRepository.findAll().get(0).getId();
        MockHttpServletRequestBuilder req = get(STATUS_CONTROLLER_PATH + ID_PATH_VAR, id);

        String respAsString = testUtils.getPerfomUnauthorizedResultAsString(req);
        TaskStatus taskStatus = fromJSON(respAsString, new TypeReference<>() { });
        assertEquals(taskStatus.getName(), DEFAULT_TASK_STATUS_1.getName());
    }

    @Test
    void testTaskStatusGetAll() throws Exception {
        testUtils.addTaskStatusUnderUser(DEFAULT_TASK_STATUS_1, DEFAULT_USER_1);
        testUtils.addTaskStatusUnderUser(DEFAULT_TASK_STATUS_2, DEFAULT_USER_1);

        MockHttpServletRequestBuilder req = get(STATUS_CONTROLLER_PATH);
        String respAsString = testUtils.getPerfomUnauthorizedResultAsString(req);
        List<TaskStatus> list = fromJSON(respAsString, new TypeReference<>() { });
        assertThat(list).hasSize(2);
    }

    @Test
    void testTaskStatusUpdate() throws Exception {
        testUtils.addTaskStatusUnderUser(DEFAULT_TASK_STATUS_1, DEFAULT_USER_1);

        Long id = taskStatusRepository.findAll().get(0).getId();
        TaskStatusDto taskStatusNew = new TaskStatusDto("hello");
        String newTaskStatusAsString = toJSON(taskStatusNew);
        MockHttpServletRequestBuilder req = put(STATUS_CONTROLLER_PATH + ID_PATH_VAR, id)
                .content(newTaskStatusAsString)
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.performWithoutToken(req).andExpect(status().isForbidden());

        String updatedTaskStatusAsString = testUtils.getPerfomAuthorizedResultAsString(req, DEFAULT_USER_1);
        TaskStatus updatedTaskStatus = fromJSON(updatedTaskStatusAsString, new TypeReference<>() { });
        assertThat(updatedTaskStatus.getName()).isEqualTo(taskStatusNew.getName());
    }
}
