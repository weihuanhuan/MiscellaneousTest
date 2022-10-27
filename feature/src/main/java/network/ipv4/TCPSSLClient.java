package network.ipv4;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Created by JasonFitch on 8/16/2018.
 */
public class TCPSSLClient {

    static {
//        System.setProperty("javax.net.debug", "ssl,handshake");
    }

    public static void main(String[] args) throws Exception {

        SocketFactory socketFactory = createSocketFactory();

        //注意 ssl 的默认端口是 443
        Socket socket = socketFactory.createSocket("www.baidu.com", 443);
        if (socket instanceof SSLSocket) {
            SSLSocket sslSocket = (SSLSocket) socket;
            sslSocket.startHandshake();
        }
//         socket = new Socket("www.baidu.com", 80);

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        outputStream.write("GET / HTTP/1.1\r\n".getBytes());
        outputStream.write("Host: www.baidu.com\r\n".getBytes());
        outputStream.write("Connection: close\r\n".getBytes());
        outputStream.write("\r\n".getBytes());
        outputStream.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while (null != (line = br.readLine())) {
            System.out.println(line);
        }

        inputStream.close();
        outputStream.close();
        socket.close();
    }

    private static SocketFactory createSocketFactory() throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        String property = System.getProperty("java.home");
        File caCertFile = new File(property, "/lib/security/cacerts");
        FileInputStream fileInputStream = new FileInputStream(caCertFile);
        ks.load(fileInputStream, null);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(null, null);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(ks);

        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        return context.getSocketFactory();
    }
}
