package spring.security;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.security.config.SecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration("web")
@ContextConfiguration(classes = {SecurityConfig.class})
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void configureTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void ignoreTest() throws Exception {
        ResultActions perform = mockMvc.perform(post("/spring-security/ignore/test")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        );

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("ignore"));
    }


    @Test
    void permitTest() throws Exception {
        ResultActions perform = mockMvc.perform(post("/spring-security/permit/test")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        );

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("permit"));
    }

    @Test
    void basicTest() throws Exception {
        ResultActions perform = mockMvc.perform(post("/spring-security/basic/test")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        );

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("basic"));
    }

    @Test
    void digestTest() throws Exception {
        ResultActions perform = mockMvc.perform(post("/spring-security/digest/test")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        );

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("digest"));
    }

}
