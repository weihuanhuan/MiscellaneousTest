package spring.security.digest;

import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomDigestAuthenticationEntryPoint extends DigestAuthenticationEntryPoint {

    public static final String DEFAULT_DIGEST_REALM = "spring-security-test-digest-realm";
    public static final String DEFAULT_DIGEST_KEY = "spring-security-test-digest-key";

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName(DEFAULT_DIGEST_REALM);
        setKey(DEFAULT_DIGEST_KEY);

        super.afterPropertiesSet();
    }

}
