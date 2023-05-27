package spring.security.config;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityContextHolderRepository implements SecurityContextRepository {

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        // get from ThreadLocalSecurityContextHolderStrategy for current thread
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            return context;
        }

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        return emptyContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        // save into ThreadLocalSecurityContextHolderStrategy for current thread
        SecurityContextHolder.setContext(context);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return false;
    }

}
