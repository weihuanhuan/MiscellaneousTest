package spring.jdbc.aop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"spring.jdbc.aop.service", "spring.jdbc.aop.controller", "spring.jdbc.aop.aspect"})
public class AopTransactionConfig {


}
