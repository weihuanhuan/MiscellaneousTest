
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
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

    public static void initProvider(){
        ProviderUtil.deleteProvider();
        ProviderUtil.insertProvicer();
    }

    public static Socket initClient() throws Exception {
        SSLContext sslContext = contextWithoutCert();
//        SSLContext sslContext = contextWithCert();

        SocketFactory factory = sslContext.getSocketFactory();
        String hostname="localhost";
        int port = 8443;
//        hostname = "ebssec.boc.cn";
//        port = 443;
        System.out.println("##########connecting to server on "+port+" ...##########");
        Socket clientSocket = factory.createSocket(hostname, port);

        return clientSocket;
    }

    public static void main(String[] args) throws Exception {
        initProvider();
        System.setProperty("javax.net.debug", "ssl,handshake");

        PrintWriter    writer       = null;
        BufferedReader reader       = null;
        Socket         clientSocket = null;
        try {
            clientSocket = initClient();

            writer = new PrintWriter(clientSocket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //JF writer.println("GET /boc15/login.html HTTP/1.1\r\n");
            //JF 这里工行提示404，原因是println会在输出数据后自动的添加换行，而我数据本身已经提供了http报文所需的换行，导致报文格式错误
            //JF 最好发送char数组防止对端接收时解码错误。
            writer.print("GET /boc15/login.html HTTP/1.1\r\n".toCharArray());
            writer.print("Host:ebssec.boc.cn\r\n".toCharArray());
            writer.print("Accept-Protocal:SM-SSL\r\n".toCharArray());
            writer.print("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; WOW64; Trident/7.0; .NET4.0E; .NET4.0C; InfoPath.3; .NET CLR 3.5.30729; .NET CLR 2.0.50727; .NET CLR 3.0.30729)\r\n".toCharArray());
            writer.print("\r\n".toCharArray());//空行，通知服务器请求头部分到此结束
            writer.flush();

            String line;
            //JF while时会一直等待服务器数据导致阻塞进程
            if (null != (line = reader.readLine())) {
                System.out.println(line);
            }
        } finally {
            System.out.println("close resource");
            reader.close();
            writer.close();
            clientSocket.close();
        }

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
