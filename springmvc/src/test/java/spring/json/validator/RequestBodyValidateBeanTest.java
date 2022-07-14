package spring.json.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import spring.validator.RequestBodyValidateBean;
import spring.validator.RequestBodyValidateBeanController;
import spring.validator.exception.ConstraintValidationExceptionExceptionHandler;
import spring.validator.exception.MethodArgumentNotValidResponseEntityExHandler;
import spring.validator.response.ValidationErrorResponse;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//该类的全名是 org.springframework.boot.test.autoconfigure.web.servlet ,
//他是 spring boot 体系里面的，纯粹的 spring mvc 是没有该类的
//@WebMvcTest(controllers = RequestBodyValidateBeanController.class)

//这里只要添加了他 spring 就会使用集成测试并加载 ApplicationContext，
//此时如果没有配置正确的 @ContextConfiguration 就会产生失败 load an ApplicationContext 的问题
//@ExtendWith(SpringExtension.class)
public class RequestBodyValidateBeanTest {

    private RequestBodyValidateBeanRequestBuilder requestBuilder;

    @BeforeEach
    void configureTest() {
        RequestBodyValidateBeanController testedController = new RequestBodyValidateBeanController();

        //手动构建 spring 单元测试时的依赖组件
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(testedController)
                .setControllerAdvice(new ConstraintValidationExceptionExceptionHandler(), new MethodArgumentNotValidResponseEntityExHandler())
                .setMessageConverters(WebTestConfig.objectMapperHttpMessageConverter())
                .setValidator(WebTestConfig.getValidator())
                .build();

        requestBuilder = new RequestBodyValidateBeanRequestBuilder(mockMvc);
    }

    private RequestBodyValidateBean createRequestBean() {
        return WebTestBean.createRequestBean();
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

    //在 junit5 环境中测试时, spring 不对被 @Valid 标记的 bean 进行 validate,
    //这里的不校验会导致本方法提供的无效 bean 也可以被 controller 正常处理，这该如何解决？
    //如果我们在 web 容器中运行，则对 @Valid 标记的 bean 是会进行 validate 的
    //有于独立运行环境中没有 tomcat 提供的 EL 依赖导致 validator 实例化失败，
    //org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport.mvcValidator
    //所以我们需要手动在独立测试中配置 validator 来避免加载 EL
    //org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.setValidator
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
