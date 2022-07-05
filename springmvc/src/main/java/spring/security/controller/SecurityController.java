package spring.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spring-security")
public class SecurityController {

    @RequestMapping("/ignore/test")
    public String ignore() {
        return "ignore";
    }

    @RequestMapping("/permit/test")
    public String permit() {
        return "permit";
    }

    @RequestMapping("/basic/test")
    public String basic() {
        return "basic";
    }

    @RequestMapping("/digest/test")
    public String digest() {
        return "digest";
    }

}
