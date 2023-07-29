package spring.jdbc.aop.realtime.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"spring.jdbc.aop.realtime.aspect", "spring.jdbc.aop.realtime.controller", "spring.jdbc.aop.realtime.service"})
public class AopRealtimeConfig {


}
