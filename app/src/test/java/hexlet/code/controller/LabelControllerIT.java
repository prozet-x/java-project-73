package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import hexlet.code.utils.TestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static hexlet.code.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class LabelControllerIT {
    @Autowired
    private TestUtils testUtils;

    @Autowired
    private LabelRepository labelRepository;

    @AfterEach
    void clearBase() {
        testUtils.clear();
    }

    @Test
    public void testCreateNew() throws Exception {
        assertEquals(labelRepository.count(), 0);

        MockHttpServletRequestBuilder req = post(LABEL_CONTROLLER_PATH)
                .content(toJSON(defaultLabel))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithoutToken(req)
                .andExpect(status().isUnauthorized());

        assertEquals(labelRepository.count(), 0);

        String labelAsString = testUtils.performWithToken(req, defaultUser1)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Label label = fromJSON(labelAsString, new TypeReference<Label>() {});
        assertEquals(label.getName(), defaultLabel.getName());

        assertEquals(labelRepository.count(), 1);

        LabelDto labelDtoBadName = new LabelDto(" ");
        MockHttpServletRequestBuilder reqBadName = post(LABEL_CONTROLLER_PATH)
                .content(toJSON(labelDtoBadName))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithToken(reqBadName, defaultUser1)
                .andExpect(status().isUnprocessableEntity());
    }
}
