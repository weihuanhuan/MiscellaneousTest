package spring.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import spring.security.basic.CustomBasicAuthenticationEntryPoint;
import spring.security.digest.CustomDigestAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@ComponentScan({"spring.security.basic", "spring.security.digest", "spring.security.controller"})
public class SecurityConfig {

    @Autowired
    private CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;

    @Autowired
    private CustomDigestAuthenticationEntryPoint customDigestAuthenticationEntryPoint;

    @Order(1)
    @Configuration
    public class BypassSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/spring-security/ignore/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            //防止产生默认的 HttpSecurity 配置，具体可见其父类实现,
            //super.configure(http);
            //当前该配置如下，会默认认证所有的请求，支持 basic 和 form 两种认证方法，并且只有当请求认证成功时，才给该请求进行授权。
            //http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();

            //认证配置
            http.antMatcher("/spring-security/permit/**")
                    //授权配置
                    .authorizeRequests().antMatchers("/spring-security/permit/**").permitAll();
        }
    }

    @Order(2)
    @Configuration
    public class DigestSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            //认证配置
            http.antMatcher("/spring-security/digest/**").addFilter(getDigestAuthenticationFilter())
                    //授权配置
                    .authorizeRequests().antMatchers("/spring-security/digest/**").authenticated()
                    //异常处理
                    .and().exceptionHandling().authenticationEntryPoint(customDigestAuthenticationEntryPoint);
        }

        private DigestAuthenticationFilter getDigestAuthenticationFilter() throws Exception {
            DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
            digestAuthenticationFilter.setUserDetailsService(userDetailsServiceBean());
            digestAuthenticationFilter.setAuthenticationEntryPoint(customDigestAuthenticationEntryPoint);
            return digestAuthenticationFilter;
        }
    }

    @Order(3)
    @Configuration
    public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            //认证配置
            http.antMatcher("/spring-security/basic/**")
                    .httpBasic()
                    .authenticationEntryPoint(customBasicAuthenticationEntryPoint)
                    //授权配置
                    .and()
                    .authorizeRequests()
                    .antMatchers("/spring-security/basic/**")
                    // equal to direct call ExpressionInterceptUrlRegistry.access("hasAuthority('" + authority + "')")
                    .hasAuthority(SecurityConfigGlobal.SPRING_SECURITY_TEST_ROLE)
                    //异常处理
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(customBasicAuthenticationEntryPoint);
        }
    }

}
