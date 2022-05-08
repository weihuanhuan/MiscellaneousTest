package spring.json.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import spring.json.validator.exception.ConstraintValidationExceptionExceptionHandler;
import spring.json.validator.exception.MethodArgumentNotValidResponseEntityExHandler;
import spring.json.validator.response.ValidationErrorResponse;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//该类的全名是 org.springframework.boot.test.autoconfigure.web.servlet ,
//他是 spring boot 体系里面的，纯粹的 spring mvc 是没有该类的
//@WebMvcTest(controllers = RequestBodyValidateBeanController.class)

//集成测试 - 基于 xml 的配置
//@ContextConfiguration(locations = "classpath:spring-mvc.xml")
//集成测试 - 基于 annotation 的配置
//@ContextConfiguration(classes = RequestBodyValidateBeanController.class)

//这里只要添加了他 spring 就会使用集成测试并加载 ApplicationContext，
//此时如果没有配置正确的 @ContextConfiguration 就会产生失败 load an ApplicationContext 的问题
//@ExtendWith(SpringExtension.class)
public class RequestBodyValidateBeanTest {

    private RequestBodyValidateBeanRequestBuilder requestBuilder;

    @BeforeEach
    void configureTest() {
        RequestBodyValidateBeanController testedController = new RequestBodyValidateBeanController();

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(testedController)
                .setControllerAdvice(new ConstraintValidationExceptionExceptionHandler(), new MethodArgumentNotValidResponseEntityExHandler())
                .setMessageConverters(WebTestConfig.objectMapperHttpMessageConverter())
                .build();

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

    @Test
    public void requestBodyValidateBeanManualTest() throws Exception {
        RequestBodyValidateBean requestBean = createRequestBean();
        ResultMatcher resultMatcher = createResultMatcher();
        String api = "/request-body-validate-bean-manual";

        ResultActions resultActions = requestBuilder.executeRequest(api, requestBean, resultMatcher);

        resultActions.andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    //TODO 在 junit5 环境中测试时, spring 不对被 @Valid 标记的 bean 进行 validate,
    // 这里的不校验会导致本方法提供的无效 bean 也可以被 controller 正常处理，这该如何解决？
    // 如果我们在 web 容器中运行，则对 @Valid 标记的 bean 是会进行 validate 的
    @Test
    public void requestBodyValidateBeanAutoTest() throws Exception {
        RequestBodyValidateBean requestBean = createRequestBean();
        ResultMatcher resultMatcher = createResultMatcher();
        String api = "/request-body-validate-bean-auto";

        ResultActions resultActions = requestBuilder.executeRequest(api, requestBean, resultMatcher);

        resultActions.andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

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
