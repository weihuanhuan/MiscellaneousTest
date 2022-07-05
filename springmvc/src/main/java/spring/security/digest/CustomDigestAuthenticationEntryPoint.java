package spring.security.digest;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomDigestAuthenticationEntryPoint extends DigestAuthenticationEntryPoint {

    public static final String DEFAULT_DIGEST_REALM = "spring-security-test-digest-realm";
    public static final String DEFAULT_DIGEST_KEY = "spring-security-test-digest-key";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        super.commence(request, response, authException);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName(DEFAULT_DIGEST_REALM);
        setKey(DEFAULT_DIGEST_KEY);

        super.afterPropertiesSet();
    }

}
