package spring.jackson.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class WebMvcJacksonConfig extends DelegatingWebMvcConfiguration {

    //注入了自身的 @Bean 所生成的 ObjectMapper，
    //这里保持该引用是为了在创建 message convertor 时可以使用和 @Bean 构造的统一个对象，以保持控制全部 controller 所使用的 ObjectMapper
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> collect = converters.stream()
                .filter(c -> c.getClass().equals(MappingJackson2HttpMessageConverter.class))
                .collect(Collectors.toList());

        converters.removeAll(collect);

        MappingJackson2HttpMessageConverter converter = new CustomMappingJackson2HttpMessageConverter(objectMapper);
        converters.add(converter);

        super.extendMessageConverters(converters);
    }

    @Bean
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder json = Jackson2ObjectMapperBuilder.json();
        ObjectMapper objectMapper = json.build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

        public CustomMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
            super(objectMapper);
        }

    }

}
