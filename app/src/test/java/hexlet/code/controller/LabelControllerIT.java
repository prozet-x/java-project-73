package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import hexlet.code.utils.TestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static hexlet.code.controller.LabelController.ID;
import static hexlet.code.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

//    @BeforeEach
//    void beforeEach() throws Exception {
//        addDefaultLabel();
//    }

    @AfterEach
    void clearBase() {
        testUtils.clear();
    }

    @Test
    public void testCreateNew() throws Exception {
        assertEquals(labelRepository.count(),0);

        Label newLabel = new Label("newLabel");
        MockHttpServletRequestBuilder req = post(LABEL_CONTROLLER_PATH)
                .content(toJSON(newLabel))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithoutToken(req)
                .andExpect(status().isForbidden());

        assertEquals(labelRepository.count(), 0);

        String labelAsString = testUtils.performWithToken(req, defaultUser1)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Label label = fromJSON(labelAsString, new TypeReference<Label>() {});
        assertEquals(label.getName(), newLabel.getName());

        assertEquals(labelRepository.count(), 1);

        LabelDto labelDtoBadName = new LabelDto(" ");
        MockHttpServletRequestBuilder reqBadName = post(LABEL_CONTROLLER_PATH)
                .content(toJSON(labelDtoBadName))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithToken(reqBadName, defaultUser1)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testGetById() throws Exception {
        addLabel(defaultLabel);
        Long id = labelRepository.findAll().get(0).getId();

        MockHttpServletRequestBuilder req = get(LABEL_CONTROLLER_PATH + ID, id);
        String labelAsString = testUtils.performWithoutToken(req)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Label label = fromJSON(labelAsString, new TypeReference<Label>() {});
        assertEquals(label.getName(), defaultLabel.getName());
    }

    @Test
    public void testGetAll() throws Exception {
        addLabel(defaultLabel);
        addLabel(new LabelDto("Label"));
        assertEquals(labelRepository.count(), 2);

        MockHttpServletRequestBuilder req = get(LABEL_CONTROLLER_PATH);
        String labelsAsString = testUtils.performWithoutToken(req)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Label> labels = fromJSON(labelsAsString, new TypeReference<List<Label>>() {});
        assertEquals(labels.size(), 2);
    }

    @Test
    public void testUpdate() throws Exception {
        addLabel(defaultLabel);
        Long id = labelRepository.findAll().get(0).getId();

        String newName = "newName";
        LabelDto labelDto = new LabelDto(newName);
        MockHttpServletRequestBuilder req = put(LABEL_CONTROLLER_PATH + ID, id)
                .content(toJSON(labelDto))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithToken(req, defaultUser1)
                .andExpect(status().isOk());
        assertEquals(labelRepository.findAll().get(0).getName(), newName);

        //TEST TO UPDATE LABEL WITHOUT AUTHORIZATION
    }

    private void addLabel(LabelDto labelDto) throws Exception {
        MockHttpServletRequestBuilder req = post(LABEL_CONTROLLER_PATH)
                .content(toJSON(labelDto))
                .contentType(MediaType.APPLICATION_JSON);
        testUtils.performWithToken(req, defaultUser1);
    }
}
