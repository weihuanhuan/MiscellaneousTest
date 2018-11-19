
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
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by JasonFitch on 10/17/2018.
 */
public class GMClientMain {

    public static void main(String[] args) throws Exception {
        ProviderUtil.deleteProvider();
        ProviderUtil.insertProvicer();

        System.setProperty("javax.net.debug", "ssl,handshake");

        SSLContext sslContext = contextWithoutCert();
//        SSLContext sslContext = contextWithCert();

        SocketFactory factory = sslContext.getSocketFactory();
        String hostname="localhost";
        int port = 8443;
        hostname = "ebssec.boc.cn";
        port = 443;
        System.out.println("##########connecting to server on "+port+" ...##########");
        Socket s = factory.createSocket(hostname, port);

        PrintWriter    writer = new PrintWriter(s.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        writer.println("########################gmssl success hello#######################");
        writer.flush();
        System.out.println(reader.readLine());

        reader.close();
        writer.close();
        s.close();
    }

    private static SSLContext contextWithoutCert() throws Exception {
        //trust keystore
        KeyStore tks = KeyStore.getInstance("PKCS12", "BC");
        tks.load(new FileInputStream(Contants.SERVER_KEY_STORE), Contants.SERVER_KEY_STORE_PASSWORD.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
        tmf.init(tks);

        SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");
        context.init(null, tmf.getTrustManagers(), null);
        return context;
    }

    private static SSLContext contextWithCert() throws Exception {
        //client keystore
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(new FileInputStream(Contants.CLIENT_KEY_STORE), Contants.CLIENT_KEY_STORE_PASSWORD.toCharArray());

        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        kf.init(ks, Contants.CLIENT_KEY_STORE_PASSWORD.toCharArray());

        //trust keystore
        KeyStore tks = KeyStore.getInstance("PKCS12", "BC");
        tks.load(new FileInputStream(Contants.SERVER_KEY_STORE), Contants.SERVER_KEY_STORE_PASSWORD.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
        tmf.init(tks);

        SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");
        context.init(kf.getKeyManagers(), tmf.getTrustManagers(), null);
        return context;
    }
}
