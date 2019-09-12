package securerandom;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.bouncycastle.util.encoders.Base64;

/**
 * Created by JasonFitch on 9/9/2019.
 */
public class RSAUtils {

    private static KeyPair keyPair;
    private final static String publicKey;
    private static RSAPrivateCrtKey privateKeyObj;
    private static RSAPublicKey publicKeyObj;
    private static final String ALGORITHM_RSA = "RSA";
    private static final String PROVIDER_BC = "BC";
    public static final String RSA_NONE_PKCS1_PADDING = "RSA/None/PKCS1Padding";
    public static final int KEYSIZE = 2048;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SecureRandom random = new SecureRandom("GDGMCCd2z5q7d".getBytes());
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance(ALGORITHM_RSA, PROVIDER_BC);
        } catch (Exception e) {
            System.out.println("RSAUtils 密钥生成器实例化失败");
        }
        generator.initialize(KEYSIZE, random);
        keyPair = generator.generateKeyPair();

        privateKeyObj = (RSAPrivateCrtKey) keyPair.getPrivate();
        publicKeyObj = (RSAPublicKey) keyPair.getPublic();
        System.out.println("#########################################################################################");
        System.out.println("privateKeyObj:\n" + privateKeyObj);
        System.out.println("#########################################################################################");
        System.out.println("publicKeyObj:\n" + publicKeyObj);

        publicKey = generateBase64PublicKey();
        System.out.println("#########################################################################################");
        System.out.println("publicKey:\n" + publicKey);
    }

    public static String generateBase64PublicKey() {
        RSAPublicKey key = (RSAPublicKey) keyPair.getPublic();
        return new String(Base64.encode(key.getEncoded()));
    }

    public static String generateBase64PrivateKey() {
        RSAPrivateKey key = (RSAPrivateKey) keyPair.getPrivate();
        return new String(Base64.encode(key.getEncoded()));
    }

    public static String decryptBase64(String string) throws Exception {
        return new String(decrypt(Base64.decode(string)));
    }

    private static byte[] decrypt(byte[] bytes) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = null;
        byte[] plainText = new byte[0];
        cipher = Cipher.getInstance(RSA_NONE_PKCS1_PADDING, PROVIDER_BC);
        cipher.init(Cipher.DECRYPT_MODE, privateKeyObj);
        plainText = cipher.doFinal(bytes);
        return plainText;
    }

    //加密
    public static String encryptBase64(String string) throws Exception {
        return new String(Base64.encode(encrypt(string)));
    }

    private static byte[] encrypt(String string) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = null;
        byte[] plainText = new byte[0];
        cipher = Cipher.getInstance(RSA_NONE_PKCS1_PADDING, PROVIDER_BC);
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyObj);
        plainText = cipher.doFinal(string.getBytes("utf-8"));
        return plainText;
    }

    public static class Encoder {
        private PrivateKey mPrivateKey;
        private Cipher cipher;

        public Encoder(String privateKey) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey.getBytes()));
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
                mPrivateKey = keyFactory.generatePrivate(privatePKCS8);
                cipher = Cipher.getInstance("RSA", "BC");
            } catch (Exception e) {
            }
        }

        public String encode(String source) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, mPrivateKey);
                byte[] cipherText = cipher.doFinal(source.getBytes("utf-8"));
                return new String(Base64.encode(cipherText));
            } catch (Exception e) {
                System.out.println("RSAUtils 解密密失败");
            }
            return null;
        }
    }

    public static class Decoder {
        private PublicKey mPublicKey;
        private Cipher cipher;

        public Decoder(String publicKey) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            X509EncodedKeySpec publicX509 = new X509EncodedKeySpec(Base64.decode(publicKey.getBytes()));
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
                mPublicKey = keyFactory.generatePublic(publicX509);
                cipher = Cipher.getInstance("RSA", "BC");
            } catch (Exception e) {
                System.out.println("RSAUtils 解密密失败");
            }
        }

        public String decode(String source) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, mPublicKey);
                byte[] output = cipher.doFinal(Base64.decode(source.getBytes()));
                return new String(output, "utf-8");
            } catch (Exception e) {
                System.out.println("RSAUtils 解密密失败");
            }
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("############################################################################################");
        String text = "您好！";
        String pri =
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQChHtRZtav9QPFLxPQq2rujxUWpsZ8GPZg1xzEfrz2tWzpAOd43SJywwMMUIETpm8VOGl4pmXf5HJIgf6cskJTILx1+C4HAFDkL8OuQ1iaAEBTSk4NDwLKrkLwGgmlJaCCJVQ6Ejw6dETTRUDGTjmNT2Y96H1pYK62EjZ9wcthdpH3054q5tiyQJY3aYgp3nVq80p+63R7Uw/TRD1pmydyJEbLzBtg6O0eMA98NNlXTMkR+tA2z6vD7MXxOUt0OaGNcaz6eKzbKwhxIrdkJSLpdtOWmKjornSTZAvkrMdLK03XXVcjvfb9n2T/GTB5/bHRP5wzTi/W5QHxMhoyYxrkTAgMBAAECggEAHNNUMHyVOaj9wo2JFYWunl0z2mlBxy8L5Usu2blTcolowYbY39Eo32KNRDOFwLmysgd7ozumwDXBWvkboph3Vd1ADIXof8Hedulya6Y0myLFZusnR97Y2GL7kLqSNaTgdVF3WHXzqlwis/QB+qE12hGJXtLvKekekSF5TffuB8qWZRUTomkFFt+k//9OH6twp+hlwBKLiUAcFkZzYCyNdULR43Wm6Siwhm/lL40FcLv0H9HjcAMEQLaR87aTzW+CHiRVPEFLldBcoydh22GYCql29x+eeVgID+XiIsMufS2gDKr0lqFaq+KFbGtmI8DjpTlrauZVQzPcDi7PLH8aqQKBgQDNxME+gxTJV6o4xw8lGz9TqLVPamSG1V2yg1Cw7+tQwcXuSrQa6ugC+jES/RUZSd+xjcBiQRSV5y2eG8LoiA5X3zXGyQ05TNHTPN33hduwPoaUEvTrYbs5If3nRZ1t++YMwBqvCjLL6kSl5ojj3LGfDLTmB3LrHuNDWcE2jRE0PQKBgQDIc9h3f7y2YzG71SgZabIDxirFT5HGyq3HXyJtftF/W/Sa1wN/OA8s68TgHlE0jPXORK8KcvrxeXrpnD9mQZc19pgl6+ZiHvwswqAFb1nWp3pDrQvrfWNXjZSlwyQY/m9cEkvdJD2VE5LelgMlWDBgflHzADLNN3+gcGej1R5njwKBgBy8HUBdjcmQNHU5VyQXagCEzs0IToGFyk/jhqEu3+2nIbzlMcGQjFXeGnxMW2XsqxBgez09WWKVpgkuV0mhtl8PDLN14CLgV2zoUxb92nACS0jiXNGCFGMmHA7v6cwyIS4mpZNMGUvgqzV/vB4V87gCTkDRSXsMFTCSmCjGCmEBAoGBALJOGedyQLMcWUjzus+gLTEePT12If3qm9oUzdMIU+IuMc7qI7oua5FRx7Z0QVe1a5Enl2x8CqxxmtvimKKlBZSC3aQdyrjNRxOprB4phohiQWehrlCzIILo9ajdhGaXLQeBXuo/KmhJGQPV/MZjQ+UReGPncUkKbQSR+B7LnFgRAoGAE0PR81/SxZ1YS7oWh+IIlE15gLZxxAJsfHQmme1/mFoCsQDQWS8O3kTqoT8WD0D4QdkD5VQsU/Cpvh8fZV8IR9lsROc5s1wp0RmGu5kn1YgELqnhneacqzPX6F6OAlIJUCO6KkSxst7R6baqFWkl4YUnKSFGE0elCeIjgCim1ts=";
        String pub =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoR7UWbWr/UDxS8T0Ktq7o8VFqbGfBj2YNccxH689rVs6QDneN0icsMDDFCBE6ZvFThpeKZl3+RySIH+nLJCUyC8dfguBwBQ5C/DrkNYmgBAU0pODQ8Cyq5C8BoJpSWggiVUOhI8OnRE00VAxk45jU9mPeh9aWCuthI2fcHLYXaR99OeKubYskCWN2mIKd51avNKfut0e1MP00Q9aZsnciRGy8wbYOjtHjAPfDTZV0zJEfrQNs+rw+zF8TlLdDmhjXGs+nis2ysIcSK3ZCUi6XbTlpio6K50k2QL5KzHSytN111XI732/Z9k/xkwef2x0T+cM04v1uUB8TIaMmMa5EwIDAQAB";
        System.out.println(pri.length());
        String textEn = new RSAUtils.Encoder(pri).encode(text);
        System.out.println(textEn);
        String textDe = new RSAUtils.Decoder(pub).decode(textEn);
        System.out.println(textDe);
    }
}
