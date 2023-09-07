package spring.swagger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.swagger.bean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/swagger-test")
public class SwaggerTestController {

    private static final List<User> USERS = new ArrayList<>();

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        Optional<User> first = USERS.stream()
                .filter(user -> user.getId() == id)
                .findFirst();

        User user = first.orElse(null);
        return user;
    }

    @GetMapping("/")
    public List<User> getUsers() {
        return USERS;
    }

    @PostMapping("/")
    public User addUser(@RequestBody User user) {
        USERS.add(user);
        return user;
    }

}
