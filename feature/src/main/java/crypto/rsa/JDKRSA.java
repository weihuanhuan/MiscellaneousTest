package crypto.rsa;

import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import javax.crypto.Cipher;

/**
 * Created by JasonFitch on 9/30/2019.
 */
public class JDKRSA {

    private static final String ALGORITHOM = "RSA";

    private static final String CIPHER_PROVIDER = "SunJCE";
    private static final String TRANSFORMATION_Nopadding = "RSA/ECB/NOPADDING";
    private static final String TRANSFORMATION_PKCS1Paddiing = "RSA/ECB/PKCS1Padding";

    private static final String KEY_MODULUS_HEX =
            "00a9c151cc12e5acec59aa395d816bdaecbdc93954fe0332fa85c50dac71bb5a8243f2c303e7013b"
                    + "a1673d13d167b0407d82a9d4b0007bb67dc18b27c04915157233b903286f52fac2bafd733583b6ed"
                    + "e9be2130b7dbc96ca2e4b731f84d6f0708d0c4ebb3fc8f8c5aa5a4957143e5330cfc6cc8828fd99f"
                    + "d5f2e743280485c53b";
    private static final String KEY_PUBLIC_EXPONENT_HEX = "10001";
    private static final String KEY_PRIVATE_EXPONENT_HEX =
            "6c4f5b7860ea483df92be23425fa8211a139fda99bf4c09715b8d7f39a11573b5c4d4d5e750ad558"
                    + "333dc6224b0d2ae8a9f0e03277ec77509fa7c0f22fef12e1e3a3dfefe9855152290c4fa31a4df588"
                    + "a54da94d55914fe8c6473cfb9a1f25eccebfe93cde0fe8347f2540dc94410327fa02841975aff332"
                    + "ae974efe735019a9";

    private static final String STRING_TO_ENCRYPT = "RSA_123";
    private static final String CHAR_ENCODING = "UTF-8";


    public static void main(String[] args) throws Exception {
        PublicKey pubKey = getRSAPublicKey(new BigInteger(KEY_MODULUS_HEX, 16), new BigInteger(KEY_PUBLIC_EXPONENT_HEX, 16));
        PrivateKey privKey = getRSAPrivateKey(new BigInteger(KEY_MODULUS_HEX, 16), new BigInteger(KEY_PRIVATE_EXPONENT_HEX, 16));

        System.out.println("____Begin test RSA with Pkcs1padding, the plain text is: " + STRING_TO_ENCRYPT);
        testEncryptDecryptPkcs1padding(pubKey, privKey, STRING_TO_ENCRYPT);

        System.out.println("____Begin test RSA with Nopadding, the plain text is: " + STRING_TO_ENCRYPT);
        testEncryptDecryptNopadding(pubKey, privKey, STRING_TO_ENCRYPT);
    }


    public static void testEncryptDecryptPkcs1padding(PublicKey pubKey, PrivateKey privKey, String plainText) throws Exception {
        // 开始加密
        byte[] cipherData = encryptPkcs1padding(pubKey, plainText.getBytes(CHAR_ENCODING));

        // 输出密文
        System.out.print("Data after encryption(HEX String): ");
        dumpByteArrayToHex(cipherData);

        // 开始解密
        byte[] decryptedData = decryptPkcs1padding(privKey, cipherData);

        // 输出解密后的明文（HEX String形式）
        System.out.print("Data after decryption(HEX String): ");
        dumpByteArrayToHex(decryptedData);

        // 输出解密后的明文
        String decryptedString = new String(decryptedData, CHAR_ENCODING);
        System.out.println("Data after decryption: " + decryptedString);
    }

    public static void testEncryptDecryptNopadding(PublicKey pubKey, PrivateKey privKey, String plainText) throws Exception {
        // 开始加密
        byte[] cipherData = encryptNopadding(pubKey, plainText.getBytes(CHAR_ENCODING));

        // 输出密文
        System.out.print("Data after encryption(HEX String): ");
        dumpByteArrayToHex(cipherData);

        // 开始解密
        byte[] decryptedData = decryptNopadding(privKey, cipherData);

        // 输出解密后的明文（HEX String形式）
        System.out.print("Data after decryption(HEX String): ");
        dumpByteArrayToHex(decryptedData);

        // 输出解密后的明文
        String decryptedString = new String(decryptedData, CHAR_ENCODING);
        System.out.println("Data after decryption: " + decryptedString);
    }

    public static PublicKey getRSAPublicKey(BigInteger modulus, BigInteger publicExp) throws Exception {
        KeyFactory fact = KeyFactory.getInstance(ALGORITHOM);
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExp);
        return fact.generatePublic(publicKeySpec);
    }

    public static PrivateKey getRSAPrivateKey(BigInteger modulus, BigInteger privateExp) throws Exception {
        KeyFactory fact = KeyFactory.getInstance(ALGORITHOM);
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, privateExp);
        return fact.generatePrivate(privateKeySpec);
    }

    public static byte[] encryptPkcs1padding(PublicKey publicKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(TRANSFORMATION_PKCS1Paddiing, CIPHER_PROVIDER);
        ci.init(Cipher.ENCRYPT_MODE, publicKey);
        return ci.doFinal(data);
    }

    public static byte[] decryptPkcs1padding(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(TRANSFORMATION_PKCS1Paddiing, CIPHER_PROVIDER);
        ci.init(Cipher.DECRYPT_MODE, privateKey);
        return ci.doFinal(data);
    }

    public static byte[] encryptNopadding(PublicKey publicKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(TRANSFORMATION_Nopadding, CIPHER_PROVIDER);
        ci.init(Cipher.ENCRYPT_MODE, publicKey);
        return ci.doFinal(data);
    }

    public static byte[] decryptNopadding(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(TRANSFORMATION_Nopadding, CIPHER_PROVIDER);
        ci.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedData = ci.doFinal(data);
        // 对Nopadding方式的密文进行解密后（即ci.doFinal(data);）返回的数组中有很多前导0x00，这里仅是对文本数据加解密，应该去掉前导0x00。
        int i = 0;
        while (i < decryptedData.length && decryptedData[i] == 0) {
            i++;
        }
        return Arrays.copyOfRange(decryptedData, i, decryptedData.length);
    }

    private static void dumpByteArrayToHex(byte[] bytes) {
        System.out.println(Hex.toHexString(bytes));
    }

}
