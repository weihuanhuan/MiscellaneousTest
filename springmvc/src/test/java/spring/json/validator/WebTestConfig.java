package spring.json.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public final class WebTestConfig {

    private WebTestConfig() {
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