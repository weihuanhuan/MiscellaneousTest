package crypto.gm;

import java.security.SecureRandom;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Created by JasonFitch on 8/15/2020.
 */
public class SM4Cipher {

    private static int SM4_KEY_LENGTH = 128;

    public static byte[] generateKey() {
        CipherKeyGenerator keyGenerator = new CipherKeyGenerator();
        KeyGenerationParameters generationParameters = new KeyGenerationParameters(getSecureRandom(), SM4_KEY_LENGTH);
        keyGenerator.init(generationParameters);

        byte[] generateKey = keyGenerator.generateKey();
        return generateKey;
    }

    public static byte[] encrypt(byte[] key, byte[] originalBytes) throws InvalidCipherTextException {
        SM4Engine engine = new SM4Engine();
        BufferedBlockCipher blockCipher = new PaddedBufferedBlockCipher(engine, getBlockCipherPadding());

        KeyParameter keyParameter = new KeyParameter(key);
        blockCipher.init(true, keyParameter);

        int outputSize = blockCipher.getOutputSize(originalBytes.length);
        byte[] encryptedBytes = new byte[outputSize];

        //JF 内部会先对原始加密数据先进行填充，使得其长度满足快加密的大小要求后,在进行加密。
        int processBytes = blockCipher.processBytes(originalBytes, 0, originalBytes.length, encryptedBytes, 0);
        blockCipher.doFinal(encryptedBytes, processBytes);
        return encryptedBytes;
    }

    public static byte[] decrypt(byte[] key, byte[] encryptedBytes) throws InvalidCipherTextException {
        SM4Engine engine = new SM4Engine();
        //JF 对于块加密算法来说，其被加密的数据必须是其加密块大小的整数倍，否则无法处理，
        //   所以这里需要使用 PaddedBufferedBlockCipher 来处理填充数据的加解密问题，
        //   而这里的填充实现可以按需使用，我们默认使用 PKCS7Padding 方式来填充。
        BufferedBlockCipher blockCipher = new PaddedBufferedBlockCipher(engine, getBlockCipherPadding());

        KeyParameter keyParameter = new KeyParameter(key);
        blockCipher.init(false, keyParameter);

        byte[] decryptedBytes = new byte[encryptedBytes.length];

        //JF 解密时相反会先解密然后将解密后的数据复制到输出缓冲区，但是这里要注意的是复制时不会复制填充数据解密后的内容，
        //   所以实际的输出缓冲区的 解密字符 长度会小于输入的密文字符长度，这个差距就是拷贝时被忽略的加密时的填充数据。
        int decryptedBytesLength = blockCipher.processBytes(encryptedBytes, 0, encryptedBytes.length, decryptedBytes, 0);
        int doFinalLength = blockCipher.doFinal(decryptedBytes, decryptedBytesLength);

        //JF 这里对于 PaddedBufferedBlockCipher 来说，他不会拷贝解密了的填充数据到解密数组中，所以需要我们主动的去移除。
        //   注意实际的解密数据长度是 = 每个完整块处理长度 + 最后一块的长度，这个实际长度是去除了填充部分的数据来计算的。
        int actualDecryptedLength = doFinalLength + decryptedBytesLength;
        return removePadding(actualDecryptedLength, decryptedBytes);
    }

    private static byte[] removePadding(int actualDecryptedLength, byte[] decryptedBytes) {
        //JF 没有填充数据,直接返回解密后的数组即可。
        if (actualDecryptedLength == decryptedBytes.length) {
            return decryptedBytes;
        }

        //JF 有填充数据时，需要移除由于跳过填充数据而导致的无效解密内容，
        //   主要是因为解密数组输出的实际内容长度小于密文数组的输入内容长度，而那些被跳过的byte数组元素的值就是byte数组元素的默认值，为 (byte)0.
        byte[] removedPadding = new byte[actualDecryptedLength];
        System.arraycopy(decryptedBytes, 0, removedPadding, 0, actualDecryptedLength);
        return removedPadding;
    }

    private static BlockCipherPadding getBlockCipherPadding() {
        BlockCipherPadding cipherPadding = new PKCS7Padding();
        return cipherPadding;
    }

    public static SecureRandom getSecureRandom() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom;
    }

}
