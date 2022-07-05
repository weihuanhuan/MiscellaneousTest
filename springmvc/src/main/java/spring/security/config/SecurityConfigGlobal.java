package spring.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalAuthentication
public class SecurityConfigGlobal {

    public static final String SPRING_SECURITY_TEST_USER = "spring-security-test-user";
    public static final String SPRING_SECURITY_TEST_PASSWD = "spring-security-test-passwd";
    public static final String SPRING_SECURITY_TEST_ROLE = "spring-security-test-role";

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(SPRING_SECURITY_TEST_USER)
                .password(passwordEncoder().encode(SPRING_SECURITY_TEST_PASSWD))
                .authorities(SPRING_SECURITY_TEST_ROLE);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
