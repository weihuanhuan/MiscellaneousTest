package spring.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
// 分离 SwaggerConfig 和 TestControllerConfig ，使得在 swagger 禁用时，依旧可以加载 controller
// -Dspring.profiles.active=swagger
@Profile({"!production && swagger"})
public class SwaggerConfig {

    @Bean
    public Docket api() {
        //http://127.0.0.1:8080/springmvc/swagger-ui.html
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.ant("/swagger-test/*"))
                .apis(RequestHandlerSelectors.basePackage("spring.swagger.controller"))
                .build()
                .apiInfo(appInfo());

        // print execute stack
        Exception exception = new Exception("initialized swagger api!");
        exception.fillInStackTrace();
        exception.printStackTrace();
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
