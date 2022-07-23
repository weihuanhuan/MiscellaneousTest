package crypto.rsa;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class RSAUtilsByBouncyCastleDirect {

    private static final int STRENGTH = 1024;
    private static final int CERTAINTY = 25;
    private static final BigInteger PUBLIC_EXPONENT = BigInteger.valueOf(3L);

    private static final AsymmetricKeyParameter privateKey;
    private static final AsymmetricKeyParameter publicKey;

    static {
        SecureRandom random = new SecureRandom("GDGMCCd2z5q7d".getBytes());
        RSAKeyGenerationParameters rsaKeyGenerationParameters = new RSAKeyGenerationParameters(PUBLIC_EXPONENT, random, STRENGTH, CERTAINTY);

        RSAKeyPairGenerator rsaKeyPairGenerator = new RSAKeyPairGenerator();
        rsaKeyPairGenerator.init(rsaKeyGenerationParameters);

        AsymmetricCipherKeyPair keyPair = rsaKeyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public static void main(String[] args) throws IOException, InvalidCipherTextException {
        System.out.println("\"######################### generateBase64KeyString(privateKey) ###########################\"");
        String privateKeyString = generateBase64KeyString(privateKey);
        System.out.println("######################### generateBase64KeyString(publicKey) ###########################");
        String publicKeyString = generateBase64KeyString(publicKey);
        System.out.println("#########################################################################################");
        System.out.println("privateKeyString:\n" + privateKeyString);
        System.out.println("publicKeyString:\n" + publicKeyString);

        System.out.println("############################ selfEncryptAndDecrypt ###########################");
        selfEncryptAndDecrypt();

        System.out.println("############################ decryptFromJSEncrypt ###########################");
        decryptFromJSEncrypt();
    }

    public static void selfEncryptAndDecrypt() throws InvalidCipherTextException {
        String text = "您好！";
        byte[] bytes = text.getBytes();

        byte[] encrypt = encrypt(bytes, publicKey);
        byte[] decrypt = decrypt(encrypt, privateKey);

        String decryptString = new String(decrypt);
        System.out.println(decryptString);
    }

    public static void decryptFromJSEncrypt() throws InvalidCipherTextException {
        String encryptFromJSEncrypt = "TQd08LOJzigZH3kHOnr6XBAla6Y7P0Z5pxCCDTp1UwnXd75VWotrzgvbh5Ed6E8ye5gIzXVKWTx9LWslHF73bGuOuwNNFNfg2tcKlx1Xwkmr/s13PzEgkgcRqIG0EygSx9Ak4g0lCm9RfOSbPEAujMVvum+6QWjz/nNhVwy7C4k=";

        byte[] decode = Base64.decode(encryptFromJSEncrypt);
        byte[] decrypt = decrypt(decode, privateKey);

        String decryptString = new String(decrypt);
        System.out.println(decryptString);
    }

    public static String generateBase64KeyString(AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        //在 bouncy castle 中 public 和 private 的 key parameter 实现有共同的基类 AsymmetricKeyParameter
        //可以通过 org.bouncycastle.crypto.params.AsymmetricKeyParameter.isPrivate 来判断他们的类型
        System.out.println("asymmetricKeyParameter:\n" + asymmetricKeyParameter);

        ASN1Object asn1Object;
        if (asymmetricKeyParameter.isPrivate()) {
            RSAPrivateCrtKeyParameters rsaPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters) asymmetricKeyParameter;
            System.out.println("rsaPrivateCrtKeyParameters.getExponent()=" + rsaPrivateCrtKeyParameters.getExponent());
            System.out.println("rsaPrivateCrtKeyParameters.getPublicExponent()=" + rsaPrivateCrtKeyParameters.getPublicExponent());
            asn1Object = PrivateKeyInfoFactory.createPrivateKeyInfo(asymmetricKeyParameter);
        } else {
            RSAKeyParameters rsaKeyParameters = (RSAKeyParameters) asymmetricKeyParameter;
            System.out.println("rsaKeyParameters.getExponent()=" + rsaKeyParameters.getExponent());
            asn1Object = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricKeyParameter);
        }

        //在 bouncy castle 中 public 和 private 的 KeyInfo 实现有共同的基类 ASN1Object
        System.out.println("asn1Object:\n" + asn1Object);

        return asn1ObjectToBase64String(asn1Object);
    }

    private static String asn1ObjectToBase64String(ASN1Object asn1Object) throws IOException {
        //间接调用 org.bouncycastle.asn1.ASN1Object.getEncoded()
        ASN1Object asn1Primitive = asn1Object.toASN1Primitive();
        byte[] asn1PrimitiveEncoded = asn1Primitive.getEncoded();

        //直接调用 org.bouncycastle.asn1.ASN1Object.getEncoded()
        //不论直接或者是间接他们的 getEncoded 值是一样的。
        byte[] asn1ObjectEncoded = asn1Object.getEncoded();
        boolean equal = Arrays.equals(asn1ObjectEncoded, asn1PrimitiveEncoded);
        System.out.println("Arrays.equals(asn1ObjectEncoded, asn1PrimitiveEncoded)=" + equal);

        byte[] base64Encode = Base64.encode(asn1PrimitiveEncoded);
        return new String(base64Encode);
    }

    public static byte[] encrypt(byte[] data, AsymmetricKeyParameter key) throws InvalidCipherTextException {
        RSAEngine rsaEngine = new RSAEngine();
        PKCS1Encoding engine = new PKCS1Encoding(rsaEngine);
        engine.init(true, key);

        return engine.processBlock(data, 0, data.length);
    }

    public static byte[] decrypt(byte[] data, AsymmetricKeyParameter key) throws InvalidCipherTextException {
        RSAEngine rsaEngine = new RSAEngine();
        PKCS1Encoding engine = new PKCS1Encoding(rsaEngine);
        engine.init(false, key);

        return engine.processBlock(data, 0, data.length);
    }

}
