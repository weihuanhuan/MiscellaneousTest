package ssl.bouncycastle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

/**
 * Created by JasonFitch on 10/23/2018.
 */
public class BCServerMain {

    private static String SERVER_KEY_STORE          = "c:/Users/JasonFitch/server_ks";
    private static String SERVER_KEY_STORE_PASSWORD = "123123";


    public static void main(String[] args) throws IOException, NoSuchProviderException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, CertificateException {
        addBouncyCastleProvicer();
        String modeControl  = "(?i)";
        String providerName = ".*.*";
        String type         = ".*random.*";
        String algorithm    = ".*.*";
        queryBouncyCastleProvicer(modeControl, providerName, type, algorithm);


        KeyStore ks = KeyStore.getInstance("BKS", "BC");
        ks.load(new FileInputStream(SERVER_KEY_STORE), null);

        KeyManagerFactory kf = KeyManagerFactory.getInstance("X.509", "BCJSSE");
        kf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());

        SSLContext bcSSL = SSLContext.getInstance("TLS", "BCJSSE");
        bcSSL.init(kf.getKeyManagers(), null, null);

        ServerSocketFactory factory      = bcSSL.getServerSocketFactory();
        ServerSocket        serverSocket = factory.createServerSocket(8443);


        ((SSLServerSocket) serverSocket).setNeedClientAuth(false);
//        ((SSLServerSocket) serverSocket).setNeedClientAuth(true);

        while (true) {

            System.out.println("##########waiting for client on port 8443 ...##########");
            Socket clientSocket = serverSocket.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter    writer = new PrintWriter(clientSocket.getOutputStream());

            String data = reader.readLine();
            writer.println(data);
            writer.close();
            clientSocket.close();


        }

    }

    private static void addBouncyCastleProvicer() {

        java.security.Provider bouncyCastleProvider = new BouncyCastleProvider();
        java.security.Security.addProvider(bouncyCastleProvider);

        java.security.Provider bouncyCastleJsseProvider = new BouncyCastleJsseProvider();
        java.security.Security.addProvider(bouncyCastleJsseProvider);


    }

    private static void queryBouncyCastleProvicer(String modeControl, String providerName, String type, String algorithm) {

        String  regex   = modeControl + "^" + providerName + ":" + ".*" + type + ".*" + algorithm + ".*";
        Pattern compile = Pattern.compile(regex);

        for (Provider provider : Security.getProviders()) {
            if ("BCJSSE".equals(provider.getName()) || "BC".equals(provider.getName())) {
                System.out.println("##################################################################");
                System.out.println(provider.toString());
                System.out.println("##################################################################");
                int i = 0;
                for (Provider.Service service : provider.getServices()) {
                    String  result  = service.toString();
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
