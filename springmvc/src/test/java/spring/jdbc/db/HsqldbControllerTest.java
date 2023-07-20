package spring.jdbc.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import spring.jdbc.db.controller.HsqldbController;

// 在 HsqldbConfig 中没有使用 @ComponentScan 指定 HsqldbController 的情况下，可以手动在 @ContextConfiguration 中指定
@ContextConfiguration(classes = {HsqldbConfig.class, HsqldbController.class})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
public class HsqldbControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void configureTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void hsqldbQueryAllTest() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/hsqldb-query-all"));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("2"));
    }


    @Test
    void hsqldbManagerSwingTest() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/hsqldb-manager-swing"));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

}
