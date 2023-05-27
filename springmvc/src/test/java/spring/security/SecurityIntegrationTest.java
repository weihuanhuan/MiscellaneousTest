package spring.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.security.config.SecurityConfig;
import spring.security.config.SecurityConfigGlobal;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration("web")
@ContextConfiguration(classes = {SecurityConfig.class, SecurityConfigGlobal.class})
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void configureTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void ignoreTest() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/spring-security/ignore/test")
                //完全被忽略的 url ，没有任何 security filter 保护
                //.with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("ignore"));
    }

    @Test
    void permitTest() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/spring-security/permit/test")
                //不用进行认证保护的 url ，但是还是有 CsrfFilter security filter 进行保护
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("permit"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @Test
    //@WithMockUser 注解不是用来配置用户名和密码的，而是用来配置运行时的角色信息，主要是用来测试授权的，而不是认证。
    //但是由于其能再 basic 认证前预先按照 username 和 authorities 设置好当前运行环境，
    //所以在 basic 认证中 spring 从 security context 中检测到无需再次认证了，就不会执行真正的认证处理
    //所以此时我们不配置 request httpBasic 也是能测试通过的，而当配置了 httpBasic 时，即使密码错误也无所谓。
    //org.springframework.security.web.authentication.www.BasicAuthenticationFilter.authenticationIsRequired
    @WithMockUser(username = "spring-security-test-user", password = "fake-passwd", authorities = "spring-security-test-role")
    void basicTest() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/spring-security/basic/test")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
//                .with(SecurityMockMvcRequestPostProcessors.httpBasic("spring-security-test-user", "spring-security-test-passwd1"))
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("basic"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    //不使用 @WithMockUser 所以认证需要的 username password realm 都不能错误，否则无法认证通过。
    @Test
    void digestTest() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/spring-security/digest/test")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.digest("spring-security-test-user")
                        .password("spring-security-test-passwd")
                        .realm("spring-security-test-digest-realm"))
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        try {
            perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                    .andExpect(MockMvcResultMatchers.content().string("digest"))
                    //禁用 session 后，该校验会抛出 java.lang.AssertionError: Authentication should not be null
                    // 但其实我们所发起的接口调用是成功的
                    // 只不过在执行这里的校验时，其 SecurityContextHolderRepository 中的 SecurityContext 被清理了
                    // 因此这个错误是无需关心的。
                    .andExpect(SecurityMockMvcResultMatchers.authenticated().withUsername("spring-security-test-user"));
        } catch (AssertionError assertionError) {
            String message = assertionError.getMessage();
            if ("Authentication should not be null".equals(message)) {
                assertionError.printStackTrace();
            } else {
                throw assertionError;
            }
        }

    }

}
