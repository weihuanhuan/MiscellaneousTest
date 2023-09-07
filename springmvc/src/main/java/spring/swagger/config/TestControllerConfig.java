package spring.swagger.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

@Configuration
@ComponentScan(basePackages = {"spring.swagger.controller"})
// 虽然已经存在 WebMvcJacksonConfig extends DelegatingWebMvcConfiguration 了
// 但是我们这里还是需要再次继承 DelegatingWebMvcConfiguration ,来初始化 @EnableWebMvc 相关逻辑
// 这是因为 unit test 时，我们仅仅加载最少的配置，此时不会加载 WebMvcJacksonConfig ，也就不会初始化 @EnableWebMvc
// 这会导致我们的 unit test 环境中，不会自动的注册可用的 MappingJackson2HttpMessageConverter ，
// 而他是默认的处理 content-type 为 application/json;charset=UTF-8 的组件。
public class TestControllerConfig extends DelegatingWebMvcConfiguration {

}
