package crypto.gm;

import java.util.Arrays;
import org.bouncycastle.crypto.digests.SM3Digest;

/**
 * Created by JasonFitch on 8/15/2020.
 */
public class SM3Digester {

    public static byte[] calculateHash(byte[] originalBytes) {
        SM3Digest digest = new SM3Digest();
        digest.update(originalBytes, 0, originalBytes.length);
        byte[] hashBytes = new byte[digest.getDigestSize()];
        digest.doFinal(hashBytes, 0);
        return hashBytes;
    }

    public static boolean verifyHash(byte[] originalBytes, byte[] hashBytes) {
        byte[] calculateHash = calculateHash(originalBytes);
        if (Arrays.equals(calculateHash, hashBytes)) {
            return true;
        }
        return false;
    }

}
