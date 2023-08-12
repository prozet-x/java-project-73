package hexlet.code.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;

@RestController
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
@AllArgsConstructor
public class StatusController {
    public static final String STATUS_CONTROLLER_PATH = "/statuses";

}
