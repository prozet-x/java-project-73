package hexlet.code.controller;

import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static hexlet.code.utils.TestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    void beforeEach() throws Exception {
        testUtils.addUser(defaultUser1);
        testUtils.addTaskStatusUnderUser(defaultTaskStatus1, defaultUser1.getEmail());
    }

    @AfterEach
    void clearBase() {
        testUtils.clear();
    }

    @Test
    void testCreate() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();
        Long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        TaskDto taskDto = testUtils.fillTaskDto(testUtils.getDefaultTaskDto(), taskStatusId, userId, userId);

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
    }

    @Test
    void testGetById() {

    }
}
