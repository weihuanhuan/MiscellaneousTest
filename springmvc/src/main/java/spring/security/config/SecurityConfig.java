package spring.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
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

    @Bean
    public HttpSessionRequestCache createSecurityRequestCache() {
        //注入自定义的 RequestCache 到 ioc 容器，以使得所有的 HttpSecurity 默认可以获取到他
        HttpSessionRequestCache httpSessionRequestCache = new SecurityRequestCache();
        httpSessionRequestCache.setCreateSessionAllowed(false);
        return httpSessionRequestCache;
    }

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

            //由于每个 WebSecurityConfigurerAdapter 中提供的 HttpSecurity 的实例是隔离的，
            // 所以这里手动创建一个新的 RequestCache 实例，将其传递给 HttpSecurity ，
            // 以防止其自动从 ioc 容器中获取，并可以自定义其配置
            // 注意下面的 createSecurityRequestCache 方法是被 @Bean 标记的，即使我们手动调用他，其所创建的对象也会被注入 spring ioc
            // 因此为了避免这个自定义配置的 RequestCache 实例被注入 ioc 中，使得其他 HttpSecurity 可以使用，我们要手动的直接 new 他。
            //HttpSessionRequestCache requestCache = createSecurityRequestCache();
            HttpSessionRequestCache httpSessionRequestCache = new SecurityRequestCache();
            httpSessionRequestCache.setCreateSessionAllowed(true);
            http.requestCache().requestCache(httpSessionRequestCache);

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

            //使用 SecurityContextHolderRepository 来修改默认的 SecurityContextRepository.saveContext 行为，防止其创建 session
            SecurityContextRepository securityContextHolderRepository = new SecurityContextHolderRepository();

            //认证配置
            http.antMatcher("/spring-security/digest/**").addFilter(getDigestAuthenticationFilter())
                    //授权配置
                    .authorizeRequests().antMatchers("/spring-security/digest/**").authenticated()
                    //异常处理
                    .and().exceptionHandling().authenticationEntryPoint(customDigestAuthenticationEntryPoint)
                    .and().sessionManagement().disable()
                    .securityContext().securityContextRepository(securityContextHolderRepository);

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
