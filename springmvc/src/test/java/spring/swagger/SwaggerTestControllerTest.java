package spring.swagger;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.swagger.bean.User;
import spring.swagger.config.SwaggerConfig;
import spring.swagger.config.TestControllerConfig;

@ContextConfiguration(classes = {SwaggerConfig.class, TestControllerConfig.class})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
public class SwaggerTestControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void configureTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void addUserAbsolute() throws Exception {
        User user = createUser("name");

        MockHttpServletRequestBuilder postRequest = createPostRequest("/swagger-test/", user);

        ResultActions perform = mockMvc.perform(postRequest);

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void addUserRelative() throws Exception {
        User user = createUser("name");

        MockHttpServletRequestBuilder postRequest = createPostRequest("/swagger-test", user);

        ResultActions perform = mockMvc.perform(postRequest);

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void addUserAndGetUserTest() throws Exception {
        User user = createUser("name");

        MockHttpServletRequestBuilder postRequest = createPostRequest("/swagger-test", user);

        ResultActions perform = mockMvc.perform(postRequest);

        // 这里提示响应代码是 404， 但我们明明是存在该 controller 的，因为将其部署到 tomcat 中是可以访问的，为什么 test 时就不行？
        // [main] WARN org.springframework.web.servlet.PageNotFound - No mapping for POST /swagger-test
        // 随后发现，这里是 @PostMapping 设置的 request path 导致的，具体的解释见 SwaggerTestController.addUserRelative 方法的说明
        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                //这里是断言 json 的结构是否相同的，而不是简单的断言 json string 是否相同的，所以需要依赖 org.skyscreamer:jsonassert ，
                //而默认情况下没有该测试依赖，导致 java.lang.ClassNotFoundException: org.skyscreamer.jsonassert.JSONAssert
                .andExpect(MockMvcResultMatchers.content().json(toJson(user)));
    }

    private MockHttpServletRequestBuilder createPostRequest(String url, User user) {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(toJson(user));
        return mockHttpServletRequestBuilder;
    }

    private User createUser(String name) {
        User user = new User();
        user.setId(name.length());
        user.setName(name);
        user.setAge(name.length());
        user.setEmail(name + "@gmail.com");
        return user;
    }

    private String toJson(User user) {
        Gson gson = new Gson();
        String toJson = gson.toJson(user);
        return toJson;
    }

}
