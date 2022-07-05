package spring.security.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;

/**
 * 注册 org.springframework.web.filter.DelegatingFilterProxy 来加载 springSecurityFilterChain
 * {@link AbstractSecurityWebApplicationInitializer#onStartup(ServletContext)}
 */
public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {

    @Override
    protected EnumSet<DispatcherType> getSecurityDispatcherTypes() {
        return super.getSecurityDispatcherTypes();
    }

    @Override
    protected boolean isAsyncSecuritySupported() {
        return super.isAsyncSecuritySupported();
    }

}
