package spring.swagger;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

@ContextConfiguration(classes = {SwaggerConfig.class})
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
    void addUserAndGetUserTest() throws Exception {
        User user = createUser("name");
        String toJson = new Gson().toJson(user);

        MockHttpServletRequestBuilder content = MockMvcRequestBuilders
                .post("/swagger-test")
                .content(toJson);
        ResultActions perform = mockMvc.perform(content);

        //TODO 这里提示响应代码是 404， 但我们明明是存在该 controller 的，因为将其部署到 tomcat 中是可以访问的，为什么 test 时就不行？
        // [main] WARN org.springframework.web.servlet.PageNotFound - No mapping for POST /swagger-test
        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().json(toJson));
    }

    private User createUser(String name) {
        User user = new User();
        user.setId(name.length());
        user.setName(name);
        user.setAge(name.length());
        user.setEmail(name + "@gmail.com");
        return user;
    }

}
