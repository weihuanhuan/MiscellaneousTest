import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class Test {


    private static String SERVER_KEY_STORE = "c:/Users/JasonFitch/server_ks";
    private static String SERVER_KEY_STORE_PASSWORD = "123123";

    public static void main(String[] args) throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        ProviderUtil.insertProvicer();
        String modeControl = "(?i)";
        String providerName = ".*SunJCE.*";
        String type = ".*KeyGenerator.*";
        String algorithm = ".*.*";
        ProviderUtil.queryProvicer(modeControl, providerName, type, algorithm);



        KeyStore ks = KeyStore.getInstance("jceks","SunJCE");
        ks.load(new FileInputStream(SERVER_KEY_STORE), null);

    }
}
