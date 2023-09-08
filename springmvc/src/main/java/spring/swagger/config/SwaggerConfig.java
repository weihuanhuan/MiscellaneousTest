package spring.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import spring.swagger.condition.SwaggerInitialize;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    // 对于 @PropertySource 类，需要手动添加 @Configuration ，否则内部类不会被作为配置类处理
    // 而 @EnableSwagger2 自己 @Import({Swagger2DocumentationConfiguration.class}) 的中添加了 @Configuration
    @Configuration
    @PropertySource(value = "classpath:spring-swagger.properties", ignoreResourceNotFound = true)
    @Order(-1)
    public static class InitializeProperty {
    }

    @EnableSwagger2
    // 分离 SwaggerConfig 和 TestControllerConfig ，使得在 swagger 禁用时，依旧可以加载 controller
    // -Dspring.profiles.active=swagger
    // @Profile({"!production && swagger"})
    // 使用独立的注解来控制 swagger api 的加载，避免使用影响面较大的 @Profile
    // -Dinitialize.swagger.allowed=true
    @SwaggerInitialize
    @Order(0)
    public static class InitializeSwagger {

        static {
            String activeProperty = System.getProperty("spring.profiles.active");
            System.out.println("############## " + "spring.profiles.active=" + activeProperty + " ##############");
            String allowedProperty = System.getProperty("initialize.swagger.allowed");
            System.out.println("############## " + "initialize.swagger.allowed=" + allowedProperty + " ##############");
        }

        @Bean
        public Docket api() {
            //http://127.0.0.1:8080/springmvc/swagger-ui.html
            Docket docket = new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .paths(PathSelectors.ant("/swagger-test/*"))
                    .apis(RequestHandlerSelectors.basePackage("spring.swagger.controller"))
                    .build()
                    .apiInfo(appInfo());

            System.out.println("############## " + "initialized swagger api!" + " ##############");
            return docket;
        }

        private ApiInfo appInfo() {
            return new ApiInfo(
                    "Demo api",
                    "Sample api",
                    "1.0",
                    "http://localhost/termsOfService",
                    new Contact("demo", "https://demo.com", "demo@gmail.com"),
                    "demo license",
                    "https://demo-license",
                    Collections.emptyList()
            );
        }
    }

}
