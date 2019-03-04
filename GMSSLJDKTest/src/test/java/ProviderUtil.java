import com.gm.crypto.provider.SunJCE;
import gm.security.provider.internal.GMProvider;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class ProviderUtil {

    public static void insertProvicer() {

//        java.security.Provider bouncyCastleProvider = new BouncyCastleProvider();
//        java.security.Security.addProvider(bouncyCastleProvider);

//        java.security.Provider bouncyCastleJsseProvider = new BouncyCastleJsseProvider();
//        java.security.Security.addProvider(bouncyCastleJsseProvider);

        java.security.Provider sunJCE = new SunJCE();
        Security.insertProviderAt(sunJCE, 1);

        java.security.Provider gmProvider = new GMProvider();
        Security.insertProviderAt(gmProvider, 2);

    }

    public static void queryProvicer(String modeControl, String providerName, String type, String algorithm) {

        String regex = modeControl + "^" + providerName + ":" + "\\s+" + type + "." + algorithm + ".*";
        Pattern compile = Pattern.compile(regex);

        Provider[] providers = Security.getProviders();
        System.out.println(Arrays.deepToString(providers));
        for (java.security.Provider provider : Security.getProviders()) {
            if ("SunJSSE".equals(provider.getName()) || "GMJSSE".equals(provider.getName()) || true) {
                System.out.println("##################################################################");
                System.out.println(provider.toString());
                System.out.println("##################################################################");
                int i = 0;
                for (java.security.Provider.Service service : provider.getServices()) {
                    String result = service.toString();
                    Matcher matcher = compile.matcher(result);
                    if (matcher.find()) {
                        ++i;
                        System.out.println(result);
                    }
                }
                System.out.println("In mode " + modeControl + " found [" + providerName + type + algorithm + "] service count:" + i);
            }
        }
    }
}
