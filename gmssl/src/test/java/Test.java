import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class Test {


    private static String SERVER_KEY_STORE = "c:/Users/JasonFitch/server_ks";
    private static String SERVER_KEY_STORE_PASSWORD = "123123";

    public static void main(String[] args) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        ProviderUtil.deleteProvider();
        ProviderUtil.insertProvicer();
        String modeControl = "(?i)";
        String providerName = ".*bc.*";
        String type = ".*keystore.*";
        String algorithm = ".*.*";
        ProviderUtil.queryProvicer(modeControl, providerName, type, algorithm);


        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(new FileInputStream(Contants.SERVER_KEY_STORE), Contants.SERVER_KEY_STORE_PASSWORD.toCharArray());
        Enumeration<String> aliases = ks.aliases();
        if (aliases.hasMoreElements()) {
            String        alias       = aliases.nextElement();
            System.out.println("###alias:"+alias);

            int index = 0;
            Certificate[] certChain = ks.getCertificateChain(alias);
            for (Certificate cert : certChain) {
//                System.out.println("" + ++index + "########################################certType:" + cert.getType());
//                System.out.println(cert);
            }
        }

    }
}
