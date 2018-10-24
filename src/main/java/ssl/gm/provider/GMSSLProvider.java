package ssl.gm.provider;

import java.security.Provider;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class GMSSLProvider extends Provider {
    /**
     * Constructs a provider with the specified name, version number,
     * and information.
     *
     * @param name    the provider name.
     * @param version the provider version number.
     * @param info    a description of the provider and its services.
     */
    protected GMSSLProvider(String name, double version, String info) {
        super(name, version, info);
    }

    public GMSSLProvider() {
        this("GMSSLProvider", 0.1, "Provider GMSSLProvider Impl");
    }
}
