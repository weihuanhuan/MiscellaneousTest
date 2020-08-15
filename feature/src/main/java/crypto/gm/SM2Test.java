package crypto.gm;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/**
 * Created by JasonFitch on 8/15/2020.
 */
public class SM2Test extends GMTest {

    public static void main(String[] args) throws InvalidCipherTextException {

        SM2Test();

    }

    private static void SM2Test() throws InvalidCipherTextException {

        System.out.println("############## SM2CipherTest ############");
        SM2CipherTest();

    }

    private static void SM2CipherTest() throws InvalidCipherTextException {
        String sourceString = "hello";
        byte[] sourceBytes = getSourceBytes(sourceString);

        AsymmetricCipherKeyPair keyPair = SM2Cipher.generateKeyPair();

        AsymmetricKeyParameter aPublic = keyPair.getPublic();
        byte[] encryptBytes = SM2Cipher.encrypt(aPublic, sourceBytes);

        AsymmetricKeyParameter aPrivate = keyPair.getPrivate();
        byte[] decryptedBytes = SM2Cipher.decrypt(aPrivate, encryptBytes);

        String decryptedString = getDecryptedString(decryptedBytes);
        boolean equals = sourceString.equals(decryptedString);
        System.out.println("sourceString.equals(decryptedString): " + equals);
    }
}
