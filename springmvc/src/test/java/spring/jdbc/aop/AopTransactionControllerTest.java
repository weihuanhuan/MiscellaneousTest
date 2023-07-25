package spring.jdbc.aop;

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
import spring.jdbc.aop.config.AopTransactionConfig;
import spring.jdbc.db.config.HsqldbConfig;
import spring.jdbc.mybatis.config.MyBatisConfig;
import spring.jdbc.transaction.config.MybatisTransactionConfig;

@ContextConfiguration(classes = {HsqldbConfig.class, MyBatisConfig.class, MybatisTransactionConfig.class, AopTransactionConfig.class})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AopTransactionControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void configureTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Order(1)
    void insertFailWithTransactional() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/mybatisAopTransaction/update-success-with-transactional"));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("update-config-2"));

        System.out.println("\nmybatisRedisListTest:");
        mybatisRedisListTest("2");
    }

    private void mybatisRedisListTest(String count) throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/mybatisRedis/list"));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(count));
    }

}
