
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

    private static String CLIENT_KEY_STORE          = "c:/Users/JasonFitch/client_ks";
    private static String CLIENT_KEY_STORE_PASSWORD = "456456";

    public static void main(String[] args) throws Exception {
        ProviderUtil.insertProvicer();

        System.setProperty("javax.net.debug", "ssl,handshake");
        System.setProperty("javax.net.ssl.trustStore", CLIENT_KEY_STORE);
//        如果不设置这个系统属性则会抛出如下异常:
//        Exception in thread "main" javax.net.ssl.SSLException: Connection has been shutdown: javax.net.ssl.SSLHandshakeException: gm.security.validator.ValidatorException: PKIX path building failed: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//                at gm.security.ssl.SSLSocketImpl.checkEOF(SSLSocketImpl.java:1533)
//        Caused by: javax.net.ssl.SSLHandshakeException: gm.security.validator.ValidatorException: PKIX path building failed: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//                at gm.security.ssl.Alerts.getSSLException(Alerts.java:192)
//        Caused by: gm.security.validator.ValidatorException: PKIX path building failed: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//                at gm.security.validator.PKIXValidator.doBuild(PKIXValidator.java:397)
//        Caused by: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//                at org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi.engineBuild(Unknown Source)

        Socket s = clientWithoutCert();
//        Socket s = clientWithCert();

        PrintWriter    writer = new PrintWriter(s.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        writer.println("########################gmssl success hello#######################");
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
        KeyStore ks = KeyStore.getInstance("jceks", "SunJCE");
        ks.load(new FileInputStream(CLIENT_KEY_STORE), null);

        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        kf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());

        SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");
        context.init(kf.getKeyManagers(), null, null);

        SocketFactory factory = context.getSocketFactory();
        System.out.println("##########connecting to server on prot 8443 with cert...##########");
        Socket s = factory.createSocket("localhost", 8443);
        return s;
    }
}
