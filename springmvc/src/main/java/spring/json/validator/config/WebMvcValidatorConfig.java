package spring.json.validator.config;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.MessageInterpolator;

//该类的相关背景具体见 @EnableWebMvc 的 java doc,
//而 @EnableWebMvc 则相当于是 xml 配置模式中的 <mvc:annotation-driven> 元素
//@EnableWebMvc
@Configuration
public class WebMvcValidatorConfig extends DelegatingWebMvcConfiguration {

    @Override
    public Validator mvcValidator() {
        return super.mvcValidator();
    }

    @Bean(name = "spring.json.validator.config.WebMvcValidatorConfig.parameterMessageInterpolatorValidator")
    @Override
    protected Validator getValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setMessageInterpolator(messageInterpolator());
        return localValidatorFactoryBean;
    }

    public MessageInterpolator messageInterpolator() {
        return new ParameterMessageInterpolator();
    }

}
