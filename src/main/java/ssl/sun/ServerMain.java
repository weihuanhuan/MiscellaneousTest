package ssl.sun;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

/**
 * Created by JasonFitch on 10/17/2018.
 */
public class ServerMain {

    public static void main(String[] args) {

        //动态注册
//        java.security.Provider provider = new javaxt.ssl.SSLProvider();
//        java.security.Security.addProvider(provider);
//        SSLContext sslc = SSLContext.getInstance("TLS", "SSLProvider");

        try {
            SSLServer.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

class SSLServer extends Thread {

    private Socket socket;
    private static String SERVER_KEY_STORE = "c:/Users/JasonFitch/server_ks";
    private static String SERVER_KEY_STORE_PASSWORD = "123123";

    public SSLServer(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            String data = reader.readLine();
            writer.println(data);
            writer.close();
            socket.close();
        } catch (IOException e) {
        }
    }

    public static void init() throws Exception {

        System.setProperty("javax.net.ssl.trustStore", SERVER_KEY_STORE);

        SSLContext context = SSLContext.getInstance("TLS");

        KeyStore ks = KeyStore.getInstance("jceks");
        ks.load(new FileInputStream(SERVER_KEY_STORE), null);

        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
        kf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());

        context.init(kf.getKeyManagers(), null, null);


        ServerSocketFactory factory = context.getServerSocketFactory();
        ServerSocket _socket = factory.createServerSocket(8443);
        ((SSLServerSocket) _socket).setNeedClientAuth(false);
//        ((SSLServerSocket) _socket).setNeedClientAuth(true);

        while (true) {
            System.out.println("##########waiting for client on port 8443 ...##########");
            Socket socket = _socket.accept();
            SSLServer sslServer = new SSLServer(socket);
            sslServer.start();
        }
    }
}
