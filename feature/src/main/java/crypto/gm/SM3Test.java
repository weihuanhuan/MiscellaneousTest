package crypto.gm;

import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Created by JasonFitch on 8/15/2020.
 */
public class SM3Test extends GMTest {

    public static void main(String[] args) throws InvalidCipherTextException {

        SM3Test();

    }

    private static void SM3Test() {

        System.out.println("############## SM3DigesterTest ############");
        SM3DigesterTest();

    }

    private static void SM3DigesterTest() {
        String sourceString = "hello";
        byte[] sourceBytes = getSourceBytes(sourceString);

        byte[] calculateHash = SM3Digester.calculateHash(sourceBytes);

        boolean verifyHash = SM3Digester.verifyHash(sourceBytes, calculateHash);
        System.out.println("verifyHash(sourceBytes, calculateHash): " + verifyHash);
    }
}
