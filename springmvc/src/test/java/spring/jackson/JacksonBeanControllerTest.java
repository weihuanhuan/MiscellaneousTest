package spring.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.jackson.bean.RedisCluster;
import spring.jackson.bean.RedisMaster;
import spring.jackson.bean.RedisSentinel;
import spring.jackson.config.WebMvcJacksonConfig;
import spring.jackson.controller.JacksonBeanController;

//注意集成测试时， spring test 还是仅仅加载最少的 bean,
//由于我们的 controller 包并没有在 config class 中使用 ComponentScan 中指定，所以他是不会被加载的。
//为了在测试中读取我们的 controller 的实现类 ，我们应该将其直接加入到 @ContextConfiguration 中
@ContextConfiguration(classes = {WebMvcJacksonConfig.class, JacksonBeanController.class})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
public class JacksonBeanControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void configureTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void redisBaseTest() throws Exception {
        RedisCluster redisCluster = new RedisCluster();
        redisCluster.setMode("cluster");
        redisCluster.setRole("master");

        String writeValueAsString = objectMapper.writeValueAsString(redisCluster);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/jackson-redis-base")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(writeValueAsString));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void redisClusterTest() throws Exception {
        RedisCluster redisCluster = new RedisCluster();
        redisCluster.setMode("cluster");
        redisCluster.setRole("replica");

        String writeValueAsString = objectMapper.writeValueAsString(redisCluster);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/jackson-redis-cluster")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(writeValueAsString));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void redisSentinelTest() throws Exception {
        RedisSentinel redisSentinel = new RedisSentinel();
        redisSentinel.setMode("sentinel");
        redisSentinel.setRole("sentinel");

        String writeValueAsString = objectMapper.writeValueAsString(redisSentinel);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/jackson-redis-base")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(writeValueAsString));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void redisFakeMasterTest() throws Exception {
        RedisMaster redisMaster = new RedisMaster();
        redisMaster.setMode("master-salve-fake");
        redisMaster.setRole("replica");

        String writeValueAsString = objectMapper.writeValueAsString(redisMaster);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/jackson-redis-base")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(writeValueAsString));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void redisFakeClusterTest() throws Exception {
        RedisCluster redisCluster = new RedisCluster();
        redisCluster.setMode("cluster-fake");
        redisCluster.setRole("master");

        String writeValueAsString = objectMapper.writeValueAsString(redisCluster);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/jackson-redis-cluster")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(writeValueAsString));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void redisMissingFieldTest() throws Exception {
        RedisCluster redisCluster = new RedisCluster();
//        redisCluster.setMode("cluster");
        redisCluster.setRole("master");

        String writeValueAsString = objectMapper.writeValueAsString(redisCluster);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/jackson-redis-base")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(writeValueAsString));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }


}
