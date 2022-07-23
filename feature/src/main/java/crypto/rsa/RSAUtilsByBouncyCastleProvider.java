package crypto.rsa;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.util.encoders.Base64;

/**
 * Created by JasonFitch on 9/9/2019.
 */
public class RSAUtilsByBouncyCastleProvider {

    private static KeyPair keyPair;
    private final static String publicKey;
    private final static String privateKey;
    private static RSAPrivateCrtKey privateKeyObj;
    private static RSAPublicKey publicKeyObj;
    private static final String ALGORITHM_RSA = "RSA";
    private static final String PROVIDER_BC = "BC";
    public static final String RSA_NONE_PKCS1_PADDING = "RSA/None/PKCS1Padding";
    public static final int KEYSIZE = 2048;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //使用确定的随机数种子来初始化可预测的随机数
        SecureRandom random = new SecureRandom("GDGMCCd2z5q7d".getBytes());
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance(ALGORITHM_RSA, PROVIDER_BC);
        } catch (Exception e) {
            System.out.println("RSAUtils 密钥生成器实例化失败");
        }
        //生成全新的 rsa 公私钥对
        generator.initialize(KEYSIZE, random);
        keyPair = generator.generateKeyPair();

        //打印公私钥的对象结构，其可以使用 java.security.Key.getEncoded 转化为字节数组的形式来表达。
        privateKeyObj = (RSAPrivateCrtKey) keyPair.getPrivate();
        publicKeyObj = (RSAPublicKey) keyPair.getPublic();
        System.out.println("#########################################################################################");
        System.out.println("privateKeyObj:\n" + privateKeyObj);
        System.out.println("#########################################################################################");
        System.out.println("publicKeyObj:\n" + publicKeyObj);

        //使用 base64 编码公私钥对象的字节数组为 string，以用于网络传输和存储
        System.out.println("#########################################################################################");
        privateKey = generateBase64PrivateKey();
        publicKey = generateBase64PublicKey();
        System.out.println("privateKey:\n" + privateKey);
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
        //使用私钥对象来进行解密操作，这里使用 "RSA/None/PKCS1Padding" 算法，指定了加密模式和填充算法
        Cipher cipher = Cipher.getInstance(RSA_NONE_PKCS1_PADDING, PROVIDER_BC);
        cipher.init(Cipher.DECRYPT_MODE, privateKeyObj);
        byte[] plainText = cipher.doFinal(bytes);
        return plainText;
    }

    public static String encryptBase64(String string) throws Exception {
        return new String(Base64.encode(encrypt(string)));
    }

    private static byte[] encrypt(String string) throws Exception {
        //使用公钥对象来进行加密操作，这里使用 "RSA/None/PKCS1Padding" 算法，指定了加密模式和填充算法
        Cipher cipher = Cipher.getInstance(RSA_NONE_PKCS1_PADDING, PROVIDER_BC);
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyObj);
        byte[] plainText = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
        return plainText;
    }

    public static class Encoder {
        private PrivateKey mPrivateKey;
        private Cipher cipher;

        public Encoder(String privateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NoSuchPaddingException {
            //使用先前 base64 编码的私钥 string 来还原出私钥 string 的字节数组
            PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey.getBytes()));

            //将私钥字节数组还原为私钥对象来进行加密操作，这里使用 "RSA" 算法，没有指定加密模式和填充算法
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA, PROVIDER_BC);
            mPrivateKey = keyFactory.generatePrivate(privatePKCS8);
            cipher = Cipher.getInstance(ALGORITHM_RSA, PROVIDER_BC);
        }

        public String encode(String source) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
            cipher.init(Cipher.ENCRYPT_MODE, mPrivateKey);
            byte[] cipherText = cipher.doFinal(source.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.encode(cipherText));
        }
    }

    public static class Decoder {
        private PublicKey mPublicKey;
        private Cipher cipher;

        public Decoder(String publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NoSuchPaddingException {
            //使用先前 base64 编码的公钥 string 来还原出公钥 string 的字节数组
            X509EncodedKeySpec publicX509 = new X509EncodedKeySpec(Base64.decode(publicKey.getBytes()));

            //将公钥字节数组还原为公钥对象来进行解密操作,这里使用 "RSA" 算法，没有指定加密模式和填充算法
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA, PROVIDER_BC);
            mPublicKey = keyFactory.generatePublic(publicX509);
            cipher = Cipher.getInstance(ALGORITHM_RSA, PROVIDER_BC);
        }

        public String decode(String source) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            cipher.init(Cipher.DECRYPT_MODE, mPublicKey);
            byte[] output = cipher.doFinal(Base64.decode(source.getBytes()));
            return new String(output, StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("############################################################################################");
        String text = "您好！";

        //使用先前生成的公私钥来进行加解密，这里是私钥加密，公钥解密了
        String textEn = new RSAUtilsByBouncyCastleProvider.Encoder(privateKey).encode(text);
        System.out.println(textEn);
        String textDe = new RSAUtilsByBouncyCastleProvider.Decoder(publicKey).decode(textEn);
        System.out.println(textDe);

        //由于公私钥是一对密钥，他们可以互相对对方的加解密操作进行逆处理，所以这里我们可以颠倒其用处
        //使用先前生成的公私钥来进行加解密，这里是公钥加密，私钥解密了
        String encryptBase64 = encryptBase64(text);
        System.out.println(encryptBase64);
        String decryptBase64 = decryptBase64(encryptBase64);
        System.out.println(decryptBase64);
    }
}
