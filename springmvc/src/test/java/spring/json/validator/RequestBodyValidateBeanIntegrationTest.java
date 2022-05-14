package spring.json.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import spring.json.validator.config.WebMvcValidatorConfig;
import spring.json.validator.exception.MethodArgumentNotValidResponseEntityExHandler;
import spring.json.validator.response.ValidationErrorResponse;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//集成测试 - 基于 xml 的配置,所有的测试相关的 spring 配置组件已经被完整的 spring-mvc.xml 所有覆盖了
//@ContextConfiguration(locations = "classpath:spring-mvc.xml")
//集成测试 - 基于 annotation 的配置,需要补充指定所有的测试过程中相关的 spring 配置组件类,
@ContextConfiguration(classes = {
        WebMvcValidatorConfig.class,
        RequestBodyValidateBeanController.class,
        MethodArgumentNotValidResponseEntityExHandler.class
})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
class RequestBodyValidateBeanIntegrationTest {

    private RequestBodyValidateBeanRequestBuilder requestBuilder;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    void configureTest() {
        //集成测试时使用完整的 spring context 环境进行测试环境的初始化
        //此时其配置 bean 会使用 spring 的 xml 配置，或者 java 配置来获取。
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        requestBuilder = new RequestBodyValidateBeanRequestBuilder(mockMvc);
    }

    private RequestBodyValidateBean createRequestBean() {
        RequestBodyValidateBean bean = new RequestBodyValidateBean();
        bean.setFieldString("StringValue");
        bean.setFieldInteger(10);
        bean.setIpAddress("IpAddressValue");

        RequestBodyValidateChildBean childBean = new RequestBodyValidateChildBean();
        childBean.setFieldChildString(null);
        childBean.setFieldChildInteger(100);

        bean.setFieldChildObject(childBean);
        return bean;
    }

    private ResultMatcher createResultMatcher() {
        return result -> {
            String content = result.getResponse().getContentAsString();
            System.out.println("ResultMatcher context:\n" + content);

            ValidationErrorResponse response = WebTestConfig.stringToObject(content, ValidationErrorResponse.class);
            System.out.println("ResultMatcher response:\n" + response);
        };
    }

    //是这个方法的处理依赖 MethodArgumentNotValidException 异常的抛出,所以需要在测试环境中提供其正确的 ExceptionHandler 才行.
    @Test
    public void requestBodyValidateBeanAutoTest() throws Exception {
        RequestBodyValidateBean requestBean = createRequestBean();
        ResultMatcher resultMatcher = createResultMatcher();
        String api = "/request-body-validate-bean-auto";

        ResultActions resultActions = requestBuilder.executeRequest(api, requestBean, resultMatcher);

        resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    //是这个方法没有抛异常,而是返回的错误信息的 json 所以不指定 MethodArgumentNotValidResponseEntityExHandler 也可以正常处理
    @Test
    public void requestBodyValidateBeanAutoBindResultTest() throws Exception {
        RequestBodyValidateBean requestBean = createRequestBean();
        ResultMatcher resultMatcher = createResultMatcher();

        String api = "/request-body-validate-bean-auto-bind-result";
        ResultActions resultActions = requestBuilder.executeRequest(api, requestBean, resultMatcher);

        resultActions.andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }


}
