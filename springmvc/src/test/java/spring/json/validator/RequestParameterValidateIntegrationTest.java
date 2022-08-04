package spring.json.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.validator.RequestParameterValidateController;
import spring.validator.config.WebMvcValidatorConfig;
import spring.validator.exception.ConstraintValidationExceptionExceptionHandler;
import spring.validator.response.ValidationErrorResponse;

@ContextConfiguration(classes = {WebMvcValidatorConfig.class,
        RequestParameterValidateController.class,
        ConstraintValidationExceptionExceptionHandler.class})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
class RequestParameterValidateIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void configureTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void requestParameterValidateSuccessTest() throws Exception {
        String api = "/request-param-validate/test-name?desc=test-description";

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post(api));

        perform.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    //在 spring bean 组件的方法参数验证出错抛出的是 javax.validation.ConstraintViolationException 异常.
    @Test
    public void requestParameterValidateFailTest() throws Exception {
        String api = "/request-param-validate/test-name-1?desc=test";

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post(api));

        ResultMatcher resultMatcher = createResultMatcher();
        perform.andExpect(MockMvcResultMatchers.status().is4xxClientError()).andExpect(resultMatcher);
    }

    private ResultMatcher createResultMatcher() {
        return result -> {
            String content = result.getResponse().getContentAsString();
            System.out.println("ResultMatcher context:\n" + content);

            ValidationErrorResponse response = WebTestConfig.stringToObject(content, ValidationErrorResponse.class);
            System.out.println("ResultMatcher response:\n" + response);
        };
    }

}
