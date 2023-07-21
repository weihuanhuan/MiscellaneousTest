package spring.jdbc.transaction;

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
import spring.jdbc.transaction.config.MybatisTransactionConfig;

@ContextConfiguration(classes = {HsqldbConfig.class, MyBatisConfig.class, MybatisTransactionConfig.class})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MybatisTransactionControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void configureTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Order(1)
    void mybatisRedisInsertFailWithTransactionalTest() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/mybatisRedis/insert-fail-with-transactional"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        System.out.println("\nmybatisRedisListTest:");
        mybatisRedisListTest("2");
    }

    @Test
    @Order(2)
    void mybatisRedisInsertFailWithoutTransactionalTest() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/mybatisRedis/insert-fail-without-transactional"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // 需要由于该用例的测试逻辑中没有事务回滚动作，所以这里最后的实例个数是 3 个了.
        System.out.println("\nmybatisRedisListTest:");
        mybatisRedisListTest("3");
    }

    private void mybatisRedisListTest(String count) throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/mybatisRedis/list"));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(count));
    }

}
