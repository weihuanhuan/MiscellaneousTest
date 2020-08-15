package crypto.gm;

import java.nio.charset.Charset;
import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Created by JasonFitch on 8/15/2020.
 */
public class GMTest {

    public static void main(String[] args) throws InvalidCipherTextException {

        System.out.println("############## SM2Test ############");
        SM2Test.main(args);
        System.out.println();

        System.out.println("############## SM3Test ############");
        SM3Test.main(args);
        System.out.println();

        System.out.println("############## SM4Test ############");
        SM4Test.main(args);
        System.out.println();
    }

    public static byte[] getSourceBytes(String sourceString) {
        Charset charset = Charset.forName("UTF-8");
        byte[] bytes = sourceString.getBytes(charset);
        return bytes;
    }

    public static String getDecryptedString(byte[] decryptedBytes) {
        Charset charset = Charset.forName("UTF-8");
        String string = new String(decryptedBytes, charset);
        return string;
    }


}
