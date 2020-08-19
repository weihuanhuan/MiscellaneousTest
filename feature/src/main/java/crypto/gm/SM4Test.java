package crypto.gm;

import java.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Created by JasonFitch on 8/15/2020.
 */
public class SM4Test extends GMTest {

    public static void main(String[] args) throws InvalidCipherTextException {

        SM4Test();

    }

    public static void SM4Test() throws InvalidCipherTextException {

        System.out.println("############## SM4CipherTest ############");
        SM4CipherTest();

        System.out.println("############## SM4CipherPasswdKeyTest ############");
        SM4CipherPasswdKeyTest();

    }


    private static void SM4CipherTest() throws InvalidCipherTextException {
        String sourceString = "hello";
        byte[] sourceBytes = getSourceBytes(sourceString);

        byte[] key = SM4Cipher.generateKey();
        System.out.println(Arrays.toString(key));

        byte[] encryptBytes = SM4Cipher.encrypt(key, sourceBytes);

        byte[] decryptedBytes = SM4Cipher.decrypt(key, encryptBytes);

        String decryptedString = getDecryptedString(decryptedBytes);
        boolean equals = sourceString.equals(decryptedString);
        System.out.println("sourceString.equals(decryptedString): " + equals);

    }

    private static void SM4CipherPasswdKeyTest() throws InvalidCipherTextException {
        String sourceString = "hello";
        byte[] sourceBytes = getSourceBytes(sourceString);

        byte[] keyFromPasswd = SM4Cipher.generateKeyFromPasswd("password");
        System.out.println(Arrays.toString(keyFromPasswd));

        byte[] encryptBytes = SM4Cipher.encrypt(keyFromPasswd, sourceBytes);

        byte[] decryptedBytes = SM4Cipher.decrypt(keyFromPasswd, encryptBytes);

        String decryptedString = getDecryptedString(decryptedBytes);
        boolean equals = sourceString.equals(decryptedString);
        System.out.println("sourceString.equals(decryptedString): " + equals);
    }

}
