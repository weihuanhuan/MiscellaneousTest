package spring.swagger.controller;

import org.springframework.http.MediaType;
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
@RequestMapping(value = "/swagger-test")
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

    @PostMapping(value = "/")
    public User addUserAbsolute(@RequestBody User user) {
        USERS.add(user);
        return user;
    }

    // 注意由于在 controller 类上面的注解了 @RequestMapping(value = "/swagger-test") ，
    // 所以本 controller 类中的方法对应的 request path 都全部在 /swagger-test 之下。
    // 而此时如果在 addUser 方法上使用 @PostMapping(value = "/") 了，那么真实的 request path 就会是 /swagger-test/ 了，
    // 这里注意，方法上面的 @PostMapping 的 `value = "/"` 是有意义的，其会拼接到可用 request path 的结尾。
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User addUserRelative(@RequestBody User user) {
        USERS.add(user);
        return user;
    }

}
