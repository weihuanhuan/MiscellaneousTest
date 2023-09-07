package spring.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@ComponentScan(basePackages = {"spring.swagger.controller"})
@EnableSwagger2
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
