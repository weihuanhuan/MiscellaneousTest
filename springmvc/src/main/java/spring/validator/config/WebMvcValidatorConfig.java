package spring.validator.config;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;

//该类的相关背景具体见 @EnableWebMvc 的 java doc,
//而 @EnableWebMvc 则相当于是 xml 配置模式中的 <mvc:annotation-driven> 元素
//@EnableWebMvc
@Configuration
public class WebMvcValidatorConfig extends DelegatingWebMvcConfiguration {

    //1. 要开启对 spring 容器所管理组件的方法参数的校验需要想 spring 中注入 MethodValidationPostProcessor
    //2. 在 MethodValidationPostProcessor 中也需要指定一个 javax.validation.Validator，
    //3. 这里要指定我们先前创建的 org.springframework.validation.Validator 以保证生成的 javax.validation.Validator 配置是一样的。
    //Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'methodValidationPostProcessor'
    // defined in spring.validator.config.WebMvcValidatorConfig: Invocation of init method failed;
    // nested exception is javax.validation.ValidationException: HV000183: Unable to initialize 'javax.el.ExpressionFactory'.
    // Check that you have the EL dependencies on the classpath, or use ParameterMessageInterpolator instead
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();

        //获取先前配置好的 javax.validation.Validator
        Validator validator = getValidator();
        javax.validation.Validator javaxValidator = ((LocalValidatorFactoryBean) validator).getValidator();

        methodValidationPostProcessor.setValidator(javaxValidator);
        return methodValidationPostProcessor;
    }

    @Override
    public Validator mvcValidator() {
        return super.mvcValidator();
    }

    @Bean(name = "spring.validator.config.WebMvcValidatorConfig.parameterMessageInterpolatorValidator")
    @Override
    protected Validator getValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setMessageInterpolator(messageInterpolator());

        // 自定义 javax.validation.TraversableResolver 实现，防止使用默认的实现，以避免执行其内部的自动检测 JPA 实现的机制。
        TraversableResolver bypassJPADetectWithDefaultTraversableResolver = new BypassJPADetectWithDefaultTraversableResolver();
        localValidatorFactoryBean.setTraversableResolver(bypassJPADetectWithDefaultTraversableResolver);
        return localValidatorFactoryBean;
    }

    public MessageInterpolator messageInterpolator() {
        return new ParameterMessageInterpolator();
    }

}
