package spring.json.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.MessageInterpolator;

public final class WebTestConfig {

    private WebTestConfig() {
    }

    public static Validator getValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setMessageInterpolator(messageInterpolator());
        return localValidatorFactoryBean;
    }

    public static MessageInterpolator messageInterpolator() {
        return new ParameterMessageInterpolator();
    }

    public static MappingJackson2HttpMessageConverter objectMapperHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    public static <T> String objectToString(T object) throws JsonProcessingException {
        return objectMapper().writeValueAsString(object);
    }

    public static <T> T stringToObject(String stringValue, Class<T> type) throws JsonProcessingException {
        return objectMapper().readValue(stringValue, type);
    }

    public static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }

}