public class Contants {

    public static String workDir     = System.getProperty("user.dir");
    public static String resourceDir = workDir + "/gmssl/target/classes";

    public static String SERVER_KEY_STORE          = resourceDir + "/gm-certificate/gm-server.p12";
    public static String SERVER_KEY_STORE_PASSWORD = "123456";

    public static String CLIENT_KEY_STORE          = resourceDir + "/gm-certificate/gm-client.p12";
    public static String CLIENT_KEY_STORE_PASSWORD = "123456";

    static {
//        System.setProperty("javax.net.ssl.trustStore", CLIENT_KEY_STORE);
//         客户端如果不设置这个系统属性则会抛出如下异常:
//        Exception in thread "main" javax.net.ssl.SSLException: Connection has been shutdown: javax.net.ssl.SSLHandshakeException: gm.security.validator.ValidatorException: PKIX path building failed: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//                at gm.security.ssl.SSLSocketImpl.checkEOF(SSLSocketImpl.java:1533)
//        Caused by: javax.net.ssl.SSLHandshakeException: gm.security.validator.ValidatorException: PKIX path building failed: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//                at gm.security.ssl.Alerts.getSSLException(Alerts.java:192)
//        Caused by: gm.security.validator.ValidatorException: PKIX path building failed: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//                at gm.security.validator.PKIXValidator.doBuild(PKIXValidator.java:397)
//        Caused by: java.security.cert.CertPathBuilderException: Unable to find certificate chain.
//                at org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi.engineBuild(Unknown Source)

        SERVER_KEY_STORE = resourceDir+"/double-localhost.pfx";
        SERVER_KEY_STORE_PASSWORD = "DoubleCA";
    }

}
