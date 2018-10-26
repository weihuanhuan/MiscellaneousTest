import com.gm.crypto.provider.SunJCE;
import gm.security.provider.internal.GMProvider;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class ProviderUtil {

    public static void insertProvicer() {
        java.security.Provider bouncyCastleProvider = new BouncyCastleProvider();
        java.security.Security.insertProviderAt(bouncyCastleProvider, 1);

//        java.security.Provider bouncyCastleJsseProvider = new BouncyCastleJsseProvider();
//        java.security.Security.insertProviderAt(bouncyCastleJsseProvider, 2);

        java.security.Provider sunJCE = new SunJCE();
        Security.insertProviderAt(sunJCE, 2);

        java.security.Provider gmProvider = new GMProvider();
        Security.insertProviderAt(gmProvider, 3);

    }

    public static void deleteProvider() {
//        java.security.Security.removeProvider("SUN");
//        java.security.Security.removeProvider("SunRsaSign");
//        java.security.Security.removeProvider("SunEC");

        java.security.Security.removeProvider("SunJCE");
        java.security.Security.removeProvider("SunJSSE");

        java.security.Security.removeProvider("SunJGSS");
        java.security.Security.removeProvider("SunSASL");
        java.security.Security.removeProvider("XMLDSig");
        java.security.Security.removeProvider("SunPCSC");
        java.security.Security.removeProvider("SunMSCAPI");
    }

    public static void queryProvicer(String modeControl, String providerName, String type, String algorithm) {
        String  regex   = modeControl + "^" + providerName + ":" + "\\s+" + type + "\\." + algorithm + ".*";
        Pattern compile = Pattern.compile(regex);
        System.out.println(compile.pattern());

        Provider[] providers = Security.getProviders();
        System.out.println(Arrays.deepToString(providers));
        System.out.println("#############################################");

        for (java.security.Provider provider : Security.getProviders()) {
            System.out.println(provider.toString());
            System.out.println("#############################################");
            int i = 0, s = provider.size();
            for (java.security.Provider.Service service : provider.getServices()) {
                String  result  = service.toString();
                Matcher matcher = compile.matcher(result);
                if (matcher.find()) {
                    ++i;
                    System.out.println(result);
                }
            }
            System.out.println("############# found: " + i + " sum: " + s + " ##############");
        }
    }
}
