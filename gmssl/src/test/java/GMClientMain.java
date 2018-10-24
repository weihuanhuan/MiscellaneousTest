
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by JasonFitch on 10/17/2018.
 */
public class GMClientMain {

    private static String CLIENT_KEY_STORE = "c:/Users/JasonFitch/client_ks";
    private static String CLIENT_KEY_STORE_PASSWORD = "456456";

    public static void main(String[] args) throws Exception {
        ProviderUtil.insertProvicer();
        String modeControl = "(?i)";
        String providerName = ".*";
        String type = ".*keystore.*";
        String algorithm = ".*jceks.*";
        ProviderUtil.queryProvicer(modeControl, providerName, type, algorithm);


        System.setProperty("javax.net.ssl.trustStore", CLIENT_KEY_STORE);
        System.setProperty("javax.net.debug", "ssl,handshake");

        Socket s = clientWithoutCert();
//        Socket s = clientWithCert();

        PrintWriter writer = new PrintWriter(s.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(s
                .getInputStream()));
        writer.println("hello");
        writer.flush();
        System.out.println(reader.readLine());

        reader.close();
        writer.close();
        s.close();


    }


    private static Socket clientWithoutCert() throws Exception {

        SocketFactory sf = SSLSocketFactory.getDefault();
        System.out.println("##########connecting to server on prot 8443 without cert...##########");
        Socket s = sf.createSocket("localhost", 8443);
        return s;
    }

    private static Socket clientWithCert() throws Exception {

        SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");

        KeyStore ks = KeyStore.getInstance("jceks", "SunJCE");
        ks.load(new FileInputStream(CLIENT_KEY_STORE), null);

        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        kf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());

        context.init(kf.getKeyManagers(), null, null);

        SocketFactory factory = context.getSocketFactory();
        System.out.println("##########connecting to server on prot 8443 with cert...##########");
        Socket s = factory.createSocket("localhost", 8443);
        return s;

    }
}

