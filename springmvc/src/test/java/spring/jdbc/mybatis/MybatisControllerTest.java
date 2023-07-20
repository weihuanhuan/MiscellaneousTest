package spring.jdbc.mybatis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.jdbc.db.config.HsqldbConfig;
import spring.jdbc.mybatis.config.MyBatisConfig;

// MyBatisConfig 中使用了 data source 来自 HsqldbConfig ，所以 HsqldbConfig 也要指定
// 我们已经在 MyBatisConfig 中使用 @ComponentScan 指定了 controller 和 service 的包，所以这里就不需要了，他会自动处理的
@ContextConfiguration(classes = {HsqldbConfig.class, MyBatisConfig.class})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
// 由于 update/delete 用例均依赖 insert 用例，所以我们需要保证 insert 要在他们之前执行。
// 而对于整个测试类，其默认的方法执行顺序是不满足这个约束，会导致用例失败。
// 这里我们使用 @TestMethodOrder 注解在配合 org.junit.jupiter.api.Order 注解来指定测试方法的执行顺序。
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MybatisControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    // 为了能够单独执行 update/delete 用例，我们需要自动的在他们执行前调用 insert 用例
    // 而每个测试用例在执行时， junit 会单独的创建一个新的 MybatisControllerTest 实例来执行
    // 所以我们需要使用类级别的 static 域来记录当前是否执行了 mybatisRedisInsertTest ，
    // 以防止直接在 class-level 一次性执行这些测试用例时多次重复执行 insert 用例。
    private static boolean mybatisRedisInsertTestExecuted = false;
    private static boolean mybatisRedisUpdateTestExecuted = false;

    @BeforeEach
    void configureTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Order(1)
    void mybatisRedisListTest() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/mybatisRedis/list"));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("2"));
    }

    @Test
    @Order(2)
    void mybatisRedisInsertTest() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/mybatisRedis/insert"));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("default-redis-3"));

        mybatisRedisInsertTestExecuted = true;
        System.out.println("mybatisRedisInsertTestExecuted=" + mybatisRedisInsertTestExecuted);
    }

    @Test
    @Order(3)
    void mybatisRedisUpdateTest() throws Exception {
        tryExecuteMybatisRedisInsertTest();

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/mybatisRedis/update"));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("update-config-3"));

        mybatisRedisUpdateTestExecuted = true;
        System.out.println("mybatisRedisUpdateTestExecuted=" + mybatisRedisUpdateTestExecuted);
    }

    @Test
    @Order(4)
    void mybatisRedisDeleteTest() throws Exception {
        tryExecuteMybatisRedisUpdateTest();

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/mybatisRedis/delete"));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("default-redis-3"));
    }

    private void tryExecuteMybatisRedisInsertTest() throws Exception {
        if (!mybatisRedisInsertTestExecuted) {
            mybatisRedisInsertTest();
        }
    }

    private void tryExecuteMybatisRedisUpdateTest() throws Exception {
        if (!mybatisRedisUpdateTestExecuted) {
            mybatisRedisUpdateTest();
        }
    }

}
