
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by JasonFitch on 10/17/2018.
 */
public class GMClientMain {

    public static void initProvider(){
        ProviderUtil.deleteProvider();
        ProviderUtil.insertProvicer();
    }

    public static Socket initClient(boolean withCert) throws Exception {
        initProvider();

        SSLContext sslContext = withCert ? initSSLContextWithCert() : initSSLcontextWithoutCert();

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
//        System.setProperty("javax.net.debug", "ssl,handshake");

        PrintWriter    writer       = null;
        BufferedReader reader       = null;
        Socket         clientSocket = null;
        try {
            boolean withCert = false;
            clientSocket = initClient(withCert);

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
            if (null != reader) {
                reader.close();
            }
            if (null != writer) {
                writer.close();
            }
            if (null != clientSocket) {
                clientSocket.close();
            }
        }

    }

    private static SSLContext initSSLcontextWithoutCert() throws Exception {
        //trust keystore
        KeyStore tks = KeyStore.getInstance("PKCS12", "BC");
        tks.load(new FileInputStream(Contants.SERVER_KEY_STORE), Contants.SERVER_KEY_STORE_PASSWORD.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
        tmf.init(tks);

        SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");
//        客户端使用信任管理器验证服务器证书
//        当前使用真实的信任管理器会发生与 isExtended = false 时相同的异常，原因也是相同的。
//        context.init(null, tmf.getTrustManagers(), null);

        boolean isExtended = true;
//        true   无异常
//        false  如下异常
//        Exception in thread "main" java.lang.InternalError: Could not obtain X500Principal access
//        Caused by: java.security.PrivilegedActionException: java.lang.NoSuchMethodException: javax.security.auth.x500.X500Principal.<init>(gm.security.x509.X500Name)
//        Caused by: java.lang.NoSuchMethodException: javax.security.auth.x500.X500Principal.<init>(gm.security.x509.X500Name)
//        JF 构造一个虚假的信任管理器
        X509TrustManager dummyX509TrustManager = getDummyTrustManager(isExtended);

//        不设置客户端信任证书导致如下异常：
//        Exception in thread "main" javax.net.ssl.SSLException: Connection has been shutdown: javax.net.ssl.SSLHandshakeException:
//                  gm.security.validator.ValidatorException: PKIX path building failed: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//        Caused by: javax.net.ssl.SSLHandshakeException: gm.security.validator.ValidatorException: PKIX path building failed: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//        Caused by: gm.security.validator.ValidatorException: PKIX path building failed: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//        Caused by: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//        使用虚假的信任管理器，防止客户端验证服务器证书失败
        context.init(null, new X509TrustManager[]{dummyX509TrustManager}, null);

        return context;
    }

    private static SSLContext initSSLContextWithCert() throws Exception {
        //JF client keystore
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(new FileInputStream(Contants.CLIENT_KEY_STORE), Contants.CLIENT_KEY_STORE_PASSWORD.toCharArray());

        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        kf.init(ks, Contants.CLIENT_KEY_STORE_PASSWORD.toCharArray());

        //JF trust keystore
        KeyStore tks = KeyStore.getInstance("PKCS12", "BC");
        tks.load(new FileInputStream(Contants.SERVER_KEY_STORE), Contants.SERVER_KEY_STORE_PASSWORD.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
        tmf.init(tks);

        SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");
        context.init(kf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context;
    }

    private  static X509TrustManager getDummyTrustManager(boolean isExtended){
        X509TrustManager dummyX509TrustManager;
        //參考这里确定使用的信任管理器gm.security.ssl.SSLContextImpl.chooseTrustManager
        if (isExtended){
            dummyX509TrustManager = new X509ExtendedTrustManager() {
                //接口 javax.net.ssl.X509TrustManager
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                //抽象类 javax.net.ssl.X509ExtendedTrustManager
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                    //dummy 服务器信任检查 socket
                    //X509ExtendedTrustManager类型的直接调用本方法，可以通过本方法屏蔽掉信任检查
                    //而非X509ExtendedTrustManager类型的会被包装在AbstractTrustManagerWrapper中，
                    //先调用父类X509ExtendedTrustManager的本方法，然后调用gm.security.ssl.AbstractTrustManagerWrapper.checkAdditionalTrust方法
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

                }
            };
        } else {
            //非X509ExtendedTrustManager类型的会在chooseTrustManager中被包装在AbstractTrustManagerWrapper中，而后者继承了X509ExtendedTrustManager
            dummyX509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
        }

        return dummyX509TrustManager;
    }
}
