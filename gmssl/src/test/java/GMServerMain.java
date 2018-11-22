import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class GMServerMain implements Runnable{

    private static CyclicBarrier cyclicBarrier;

    public static void main(String[] args) {
        initProvider();

        cyclicBarrier = new CyclicBarrier(2);

        while (true) {
            Thread t = new Thread(new GMServerMain());
            t.start();
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (BindException be){
//          dummy java.net.BindException: Address already in use: JVM_Bind
//          BindException indirect extends  IOException
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public static void initProvider(){
        ProviderUtil.deleteProvider();
        ProviderUtil.insertProvicer();
    }

    public static ServerSocket initServerSocket() throws NoSuchProviderException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException, CertificateException {
        //server keystore
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(new FileInputStream(Contants.SERVER_KEY_STORE), Contants.SERVER_KEY_STORE_PASSWORD.toCharArray());

        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        kf.init(ks, Contants.SERVER_KEY_STORE_PASSWORD.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS", "SunJSSE");
        sslContext.init(kf.getKeyManagers(), null, null);

        ServerSocketFactory factory      = sslContext.getServerSocketFactory();
        ServerSocket        serverSocket = factory.createServerSocket(8443);
        ((SSLServerSocket) serverSocket).setNeedClientAuth(false);
//        ((SSLServerSocket) serverSocket).setNeedClientAuth(true);

        return serverSocket;
    }

    public static void startServer() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, NoSuchProviderException, IOException {
        PrintWriter    writer       = null;
        BufferedReader reader       = null;
        Socket         clientSocket = null;
        ServerSocket   serverSocket = null;
        try {
            serverSocket = initServerSocket();

            System.out.println("##########waiting for client on port 8443 ...##########");
            clientSocket = serverSocket.accept();

            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream());

            String line;
            if (null != (line = reader.readLine())) {
                System.out.println(line);
                writer.println(line);
            }
            writer.flush();

        } finally {
            System.out.println("close resource");
            if (null != reader) {
                reader.close();
            }
            if (null != writer) {
                writer.close();
            }
            if (null != clientSocket) {
                clientSocket.close();
            }
            if (null != serverSocket) {
                serverSocket.close();
            }
        }
    }

}

