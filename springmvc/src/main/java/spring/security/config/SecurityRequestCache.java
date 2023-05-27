package spring.security.config;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityRequestCache extends HttpSessionRequestCache {

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        super.saveRequest(request, response);
    }

    @Override
    public void setCreateSessionAllowed(boolean createSessionAllowed) {
        super.setCreateSessionAllowed(createSessionAllowed);
    }

}
