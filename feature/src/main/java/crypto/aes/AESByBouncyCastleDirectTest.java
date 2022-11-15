package crypto.aes;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.util.Arrays;
import java.util.Random;

public class AESByBouncyCastleDirectTest {

    public static Random random = new Random(0L);

    public static String password = "password";

    public static int iterations = 1024;

    public static int keySize = 256;

    public static void main(String[] args) throws InvalidCipherTextException {

        System.out.println("###################### PBKDF2WithHmacSHA256KeyTest ######################");
        PBKDF2WithHmacSHA256KeyTest();

        System.out.println("###################### AES_CBC_PKCS7Padding_test ######################");
        AES_CBC_PKCS7Padding_test();

    }

    private static void PBKDF2WithHmacSHA256KeyTest() {
        // salt 可以为任意 bit，这里只是为了使相同的 password 派生出不同的 key 而已
        // 一般 salt 为 128 bit 就够用了，所以 salt 为 128 / (2^3) = 16 byte
        byte[] salt = new byte[128 >> 3];

        generateRandomBytes(salt);

        printBytes("salt", salt);

        byte[] derivedKey = derivePBKDF2WithHmacSHA256Key(salt);

        printBytes("derivedKey", derivedKey);
    }

    public static byte[] derivePBKDF2WithHmacSHA256Key(byte[] salt) {
        // PBKDF2WithHmacSHA256
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password.toCharArray()), salt, iterations);
        CipherParameters cipherParameters = gen.generateDerivedMacParameters(keySize);

        return ((KeyParameter) cipherParameters).getKey();
    }

    private static void AES_CBC_PKCS7Padding_test() throws InvalidCipherTextException {
        // AES 的 BLOCK_SIZE 固定为 128 bit，所以 iv 也固定为 128 / (2^3) = 16 byte
        // org.bouncycastle.crypto.engines.AESEngine.BLOCK_SIZE
        byte[] iv = new byte[128 >> 3];
        // 当前使用 256 bit 的 key，所以 randomAESKey 为 256 / (2^3) = 32 byte
        // 也可使用 128 bit 的 key，此时 randomAESKey 为 128 / (2^3) = 16 byte
        byte[] randomAESKey = new byte[keySize >> 3];

        generateRandomBytes(iv);
        generateRandomBytes(randomAESKey);

        printBytes("iv", iv);
        printBytes("randomAESKey", randomAESKey);

        String plainText = "hello";
        byte[] plainBytes = plainText.getBytes();

        byte[] encryptedBytes = encryptBytes(plainBytes, randomAESKey, iv);

        byte[] decryptedBytes = decryptBytes(encryptedBytes, randomAESKey, iv);

        printBytes("plainBytes", plainBytes);
        printBytes("encryptedBytes", encryptedBytes);

        boolean equals = Arrays.equals(plainBytes, decryptedBytes);
        System.out.println("Arrays.equals(plainBytes, decryptedBytes)=" + equals);

    }

    public static byte[] encryptBytes(byte[] plainBytes, byte[] randomAESKey, byte[] iv) throws InvalidCipherTextException {
        // AES/CBC/PKCS7Padding
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        KeyParameter keyParameter = new KeyParameter(randomAESKey);
        ParametersWithIV parametersWithIV = new ParametersWithIV(keyParameter, iv);
        cipher.init(true, parametersWithIV);

        return cipherBytes(cipher, plainBytes);
    }

    public static byte[] decryptBytes(byte[] encryptedBytes, byte[] randomAESKey, byte[] iv) throws InvalidCipherTextException {
        // AES/CBC/PKCS7Padding
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        KeyParameter keyParameter = new KeyParameter(randomAESKey);
        ParametersWithIV parametersWithIV = new ParametersWithIV(keyParameter, iv);
        cipher.init(false, parametersWithIV);

        return cipherBytes(cipher, encryptedBytes);
    }

    public static byte[] cipherBytes(PaddedBufferedBlockCipher cipher, byte[] inputBytes) throws InvalidCipherTextException {
        int outputSize = cipher.getOutputSize(inputBytes.length);
        byte[] outputBytes = new byte[outputSize];

        int processBytes = cipher.processBytes(inputBytes, 0, inputBytes.length, outputBytes, 0);
        int doFinal = cipher.doFinal(outputBytes, processBytes);

        byte[] resultBytes = new byte[processBytes + doFinal];
        System.arraycopy(outputBytes, 0, resultBytes, 0, resultBytes.length);

        String format = String.format("cipherBytesLength:\ninputBytes=[%s], outputSize=[%s], processBytes=[%s], doFinal=[%s], resultBytes=[%s]"
                , inputBytes.length, outputSize, processBytes, doFinal, resultBytes.length);
        System.out.println(format);
        return resultBytes;
    }

    public static void generateRandomBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }

    public static void printBytes(String name, byte[] bytes) {
        String format = String.format("name=[%s]\nlength=[%s],context=[%s]", name, bytes.length, Arrays.toString(bytes));
        System.out.println(format);
    }

}
