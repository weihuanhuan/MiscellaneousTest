package network.ssl;

import org.apache.http.impl.conn.PoolingClientConnectionManager;

/**
 * Created by JasonFitch on 5/15/2019.
 */
public class TrustedCertsTest {

    static {
        System.setProperty("javax.net.ssl.trustStore","dummy");
        System.setProperty("javax.net.ssl.trustStorePassword","dummy");
    }

    public static void main(String[] args) {

        PoolingClientConnectionManager poolingClientConnectionManager = new PoolingClientConnectionManager();

//        JDK 1.8_212 情况
//        "LifecycleThread[server]-1@2608" daemon prio=5 tid=0x14 nid=NA runnable
//        java.lang.Thread.State: RUNNABLE
//        at sun.security.provider.JavaKeyStore.engineLoad(JavaKeyStore.java:787)
//        - locked <0x1351> (a java.util.Hashtable)
//        at sun.security.provider.JavaKeyStore$JKS.engineLoad(JavaKeyStore.java:56)
//        at sun.security.provider.KeyStoreDelegator.engineLoad(KeyStoreDelegator.java:224)
//        at sun.security.provider.JavaKeyStore$DualFormatJKS.engineLoad(JavaKeyStore.java:70)
//        at java.security.KeyStore.load(KeyStore.java:1445)
//        at sun.security.ssl.TrustStoreManager$TrustAnchorManager.loadKeyStore(TrustStoreManager.java:368)
//        at sun.security.ssl.TrustStoreManager$TrustAnchorManager.getTrustedCerts(TrustStoreManager.java:316)
//        - locked <0x135c> (a sun.security.ssl.TrustStoreManager$TrustAnchorManager)
//        at sun.security.ssl.TrustStoreManager.getTrustedCerts(TrustStoreManager.java:59)
//        at sun.security.ssl.TrustManagerFactoryImpl.engineInit(TrustManagerFactoryImpl.java:51)
//        at javax.net.ssl.TrustManagerFactory.init(TrustManagerFactory.java:250)
//        at org.apache.http.conn.ssl.SSLSocketFactory.createSSLContext(SSLSocketFactory.java:229)
//        at org.apache.http.conn.ssl.SSLSocketFactory.createDefaultSSLContext(SSLSocketFactory.java:358)
//        at org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory(SSLSocketFactory.java:175)
//        at org.apache.http.impl.conn.SchemeRegistryFactory.createDefault(SchemeRegistryFactory.java:49)
//        at org.apache.http.impl.conn.PoolingClientConnectionManager.<init>(PoolingClientConnectionManager.java:93)

//        createDefaultSSLContext 执行如下序列
//        return createSSLContext("TLS", null, null, null, null, null);

//        TrustStoreManager.getTrustedCerts() 会调用 如下方法,该方法会取得jvm属性中的 证书信息。
//        sun.security.ssl.TrustStoreManager.TrustStoreDescriptor.createInstance()

//        所以当存在证书的系统属性时，即使创建sslcontext的参数都是 null ，但是依旧会有证书被使用。
//        因此不推荐将证书指定为系统属性这一层面，否则容易造成意外加载使用。


    }
}
