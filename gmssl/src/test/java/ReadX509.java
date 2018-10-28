import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.X509CertificateStructure;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

public class ReadX509 {

    public static void main(String[] args) throws CertificateException, IOException, NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException, URISyntaxException {
        ProviderUtil.deleteProvider();
        ProviderUtil.insertProvicer();

        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(new FileInputStream(Contants.SERVER_KEY_STORE), Contants.SERVER_KEY_STORE_PASSWORD.toCharArray());

        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        kf.init(ks, Contants.SERVER_KEY_STORE_PASSWORD.toCharArray());

        SSLContext bcSSL = SSLContext.getInstance("TLS", "SunJSSE");
        bcSSL.init(kf.getKeyManagers(), null, null);

        String             certFileStr         = "gm-certificate/gm-server.cert";
        InputStream        certFileInputStream = ClassLoader.getSystemResourceAsStream(certFileStr);
        CertificateFactory cf                  = CertificateFactory.getInstance("X.509", "BC");
        Certificate        cert                = cf.generateCertificate(certFileInputStream);
        PublicKey          publicKey           = cert.getPublicKey();
        System.out.println(publicKey.toString());

        URL certResource = ClassLoader.getSystemResource(certFileStr);
        System.out.println(certResource);
//     file:/F:/JetBrains/IntelliJ%20IDEA/BEStest/gmssl/target/classes/gm-certificate/gm-server.cert
        Path certPath = Paths.get(certResource.toURI());
        System.out.println(certPath);
//        F:\JetBrains\IntelliJ IDEA\BEStest\gmssl\target\classes\gm-certificate\gm-server.cert
        byte[] certBytes = Files.readAllBytes(certPath);
        System.out.println(Arrays.deepToString(new byte[][]{certBytes}));
    }

}
