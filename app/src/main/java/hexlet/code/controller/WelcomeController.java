package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WelcomeController {
    @GetMapping
    public String root() {
        return "HI THERE!";
    }

    @GetMapping("welcome")
    public String welcome() {
        System.out.println("WTF");
        return "Welcome to Spring";
    }
}
