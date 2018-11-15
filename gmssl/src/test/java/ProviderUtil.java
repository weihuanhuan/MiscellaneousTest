import com.gm.crypto.provider.SunJCE;
import gm.security.provider.internal.GMProvider;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class ProviderUtil {

    public static void insertProvicer() {
        java.security.Provider bouncyCastleProvider = new BouncyCastleProvider();
        java.security.Security.insertProviderAt(bouncyCastleProvider, 1);

        java.security.Provider gmProvider = new GMProvider();
        Security.insertProviderAt(gmProvider, 2);

//        java.security.Provider sunJCE = new SunJCE();
//        Security.insertProviderAt(sunJCE, 3);

//        java.security.Provider bouncyCastleJsseProvider = new BouncyCastleJsseProvider();
//        java.security.Security.insertProviderAt(bouncyCastleJsseProvider, 4);



    }

    public static void deleteProvider() {

//        java.security.Security.removeProvider("SUN");
        //注释后生成 SecureRandom 会出错
//        Exception in thread "main" java.security.KeyStoreException: PKCS12 not found
//        Caused by: java.security.NoSuchAlgorithmException: Error constructing implementation
//          (algorithm: PKCS12, provider: BC, class: org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$BCPKCS12KeyStore)
//        Caused by: java.lang.InternalError: unable to open random source

        java.security.Security.removeProvider("SunRsaSign");
        java.security.Security.removeProvider("SunEC");

        java.security.Security.removeProvider("SunJCE");
        java.security.Security.removeProvider("SunJSSE");

        java.security.Security.removeProvider("SunJGSS");
        java.security.Security.removeProvider("SunSASL");
        java.security.Security.removeProvider("XMLDSig");
        java.security.Security.removeProvider("SunPCSC");
        java.security.Security.removeProvider("SunMSCAPI");
        //直接在配置文件【"JAVA_HOME\jre\lib\security\java.security"】中移除provider
//        1.的考虑其他程序对provider的使用，比如maven下载依赖时
//        2.由于配置文件中序号得按需递增，一次改动就得重新编排
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
