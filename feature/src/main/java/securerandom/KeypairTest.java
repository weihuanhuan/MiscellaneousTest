package securerandom;

import org.bouncycastle.util.encoders.Hex;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by JasonFitch on 9/10/2019.
 */
public class KeypairTest {

    private static byte[] PRIVATE_KEY_BYTES = new byte[]{48, -126, 4, -66, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 4, -88, 48, -126, 4, -92, 2, 1, 0, 2, -126, 1, 1, 0, -122, -92, -32, -40, -59, -25, -104, -69, -122, -77, 95, -10, 99, 109, 41, 80, -96, -6, 91, 67, -109, 97, -3, 51, 85, -39, -112, 71, 7, 74, 15, 4, -41, -26, -127, -39, 118, 83, -72, 15, 0, 4, -88, 118, -117, -41, -105, -83, -15, -10, -112, -5, 15, 59, -112, 102, -17, 37, 93, -13, -121, -33, 83, 83, 29, -38, -92, -55, -17, -124, -77, 35, -16, 11, 2, 19, -29, 17, 4, -36, 125, 75, -102, -29, -74, 23, 15, -31, -20, 73, -104, -91, 51, -110, -6, -103, 35, -108, -7, -114, 98, 103, 40, 126, 73, 52, 57, -128, -2, 18, 11, -63, -28, 0, -116, 9, -31, 33, -52, -65, -27, -128, -114, 89, 117, 123, 41, -128, -35, -19, 70, -84, 30, -48, 59, -109, -40, -38, -119, -82, 98, -97, 68, -60, 12, 9, 44, 77, 109, -18, 103, -108, -10, 39, 67, -81, -93, -73, 104, -58, -87, 36, -68, -8, -92, -78, -5, 55, 56, -31, 51, -98, 12, -36, -48, -76, 14, 16, 43, 41, -30, -81, 83, -10, 81, 104, 8, 14, -17, -127, 12, 43, -80, 37, -19, 14, -128, -17, 25, -117, 88, 69, 22, 36, -31, 96, -51, 93, 102, 111, 41, 33, 76, -80, -59, -108, -112, 42, -104, 92, 7, 48, 35, 53, -29, -114, -84, 94, -53, 15, 47, -110, 25, 11, -123, -124, 107, -62, -58, 56, -77, -74, -66, 70, -41, -21, 126, -93, 95, -13, 41, 121, 120, 118, -45, -123, 2, 3, 1, 0, 1, 2, -126, 1, 0, 25, -118, 6, -72, 55, 121, -63, -121, -72, 122, 39, -74, -91, -113, 92, 41, -42, 15, 6, -26, -97, 123, 19, -70, -4, 10, -61, -118, 37, -128, -49, -82, 124, -127, 85, -103, -7, 14, -46, 55, -50, 42, 20, 6, 64, 106, 73, 116, 14, 20, 85, -43, -71, 66, -12, 22, 2, 64, -107, 60, -113, 70, 54, 24, -22, -19, -63, 26, 40, -101, -124, 27, 73, -7, -36, -112, 7, 31, 0, 76, 14, 92, 27, -22, 27, 108, -18, 111, 77, 17, 124, -87, -63, 73, -100, -51, 21, 105, -71, -104, 95, 99, -17, -45, 92, 113, 82, -126, -4, 46, 82, 26, -103, -13, -36, -100, 55, 30, -31, 38, 121, 18, -44, 109, 117, -110, 66, 1, -48, 38, -64, 91, -69, 68, 63, -88, -99, -42, 90, 46, 120, -7, 65, -84, -80, -84, 117, -92, -119, 13, -78, 122, 105, 105, 36, 19, -6, 22, -14, -84, -125, 1, 86, -21, 53, -20, -77, 53, 57, 116, 31, -117, 41, -115, 18, -46, 115, 67, -128, 76, 88, 85, 17, 37, -123, -47, -114, 33, 77, -110, -71, -27, 114, 26, 77, 86, 46, -19, -4, 54, -56, -92, -17, -39, -51, 114, 38, -122, -104, 36, -88, 52, 27, -11, -56, 7, -32, -114, -15, -109, 102, -33, 38, 114, 85, -124, 21, -36, 102, 58, -43, -32, 106, -37, 7, -118, 72, 68, -4, -60, 67, -102, -67, -83, -14, -24, -91, 93, 74, 49, -91, 24, 83, 73, 71, -107, 2, -127, -127, 0, -69, -23, 112, -94, -74, 85, -79, -42, 116, 9, -107, 20, -82, 96, 34, 3, -117, 68, -25, 33, 110, 126, -36, -69, -65, -99, 118, 61, -10, 13, 24, -35, 96, 72, 103, 2, -28, -59, 6, 53, 59, -37, -69, -91, -62, -45, 72, 118, 76, -47, -41, 60, 117, -114, -88, 85, 77, -20, -123, -81, 39, -41, 53, -45, 120, -88, -52, -42, 124, 44, 117, 42, 5, -2, 47, 56, 125, 97, 32, -82, 108, 71, 54, -52, -38, -113, 108, 111, 32, 36, -128, 88, 56, 10, 27, 107, 123, -4, -100, 79, -84, 112, -61, -8, -84, -79, 25, 5, 47, 25, -11, 42, 62, -124, -113, -119, -65, 2, 18, 18, -85, 23, -101, 19, 31, -89, -94, -73, 2, -127, -127, 0, -73, 110, 91, 79, 32, 28, -26, 111, 74, -39, 38, -65, -26, -12, 46, -104, 44, 100, 44, 112, 96, 3, -24, -85, -55, 34, 31, -74, -97, 95, -12, 8, -115, 107, -117, -73, -2, 21, 68, -123, -127, -83, -67, 88, 22, -53, -103, 109, 25, 19, -107, -102, -107, -27, 5, 114, 73, -118, 97, 3, -22, -21, 80, -59, 61, 92, -8, 86, -25, 84, 23, -96, -116, 1, -67, 0, 2, -95, -2, -106, -2, 11, 6, -93, -38, 7, 18, 37, 114, 48, -104, -83, 83, -67, 104, 114, -120, 57, 54, -66, -99, -9, 29, 118, 49, 103, -88, 75, 109, 91, -95, 122, -38, -13, 4, 19, 47, -10, -24, 69, -42, 67, 13, -38, -53, -38, -113, -93, 2, -127, -127, 0, -128, -105, 79, 45, 9, -7, -70, -87, 54, -33, 23, -43, -1, 62, 106, 8, 98, -43, -44, 21, 92, -72, 19, -11, -89, 58, -22, 31, -100, -79, 121, -62, -124, -111, 7, 109, 67, -128, 80, -81, 61, 55, 49, 66, 78, 0, -88, 37, -52, -116, 12, -18, -94, -61, -19, 13, -13, 53, 88, -122, 116, -115, 33, -104, -123, -119, 126, 35, -120, -10, 35, 119, -114, -126, 83, 110, 36, -34, -62, -82, -102, 10, 97, -59, -18, 11, 23, -7, 51, -43, -58, -66, -104, 26, -46, -120, -53, 96, 34, -41, -94, -7, -56, 33, -12, -117, 124, -61, -65, 103, -44, 14, 4, -102, 82, 95, -63, 6, 121, -73, -84, -67, -118, -79, -101, -121, -20, 33, 2, -127, -128, 69, 42, -113, -77, -98, 96, 71, 99, -33, -51, 82, 23, 58, 119, 8, -70, -56, -62, 93, 31, 98, -107, 22, -66, -17, -16, -74, 20, -98, -76, 7, -56, -47, -54, -93, -68, -107, 74, 95, -76, 23, -39, -13, 115, -85, -28, -55, -51, -95, -113, 8, -14, 105, 99, 26, 82, -91, -8, 79, -20, -78, 64, 89, 12, -122, -66, -17, -59, -107, 36, -40, 99, -23, 103, 30, 101, 74, -104, -38, -17, 4, -116, 19, -52, -27, 1, -107, -114, 25, 40, -55, -23, 81, -110, 50, 56, 61, -75, -107, 54, -5, 73, -101, 10, 94, 70, -65, -26, -15, 45, -124, 14, 87, 81, 27, 80, 64, -35, -47, 103, -60, 11, 52, -58, 51, -24, 14, 17, 2, -127, -127, 0, -106, 61, -36, -125, -43, -80, -105, 79, 79, -93, 38, -68, -40, -38, -128, 97, 11, 115, -107, 28, 91, 125, -100, -21, 77, 5, -51, 11, -39, 67, -32, 4, -19, -70, 60, 5, 74, -49, 54, -49, -17, -81, 23, 126, -111, 92, -103, 83, -58, 6, 127, -118, 106, -81, 90, -47, -46, 110, -13, 122, 28, -14, 76, -112, -64, -42, 55, -107, -117, 29, 82, -82, -8, -66, 83, -72, 26, 116, -99, 91, 72, 92, -84, 68, 68, 5, -16, 75, -53, -39, 7, -17, 44, -63, -16, 87, -75, -1, -64, 113, 82, -81, -19, 103, 90, -23, -38, 84, 101, -88, 85, 10, 49, -37, -82, -125, -77, -33, -68, -100, -58, -10, 83, -30, -54, -114, -117, -75};

    private static KeyPair keyPair;

    public static final int KEYSIZE = 2048;
    private static final String ALGORITHM_RSA = "RSA";
    private static final String PROVIDER_BC = "BC";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static void main(String[] args) {
        //固定 SecureRandom 初始化时的种子，使得随机数的生成是可预测的。
        SecureRandom random = new SecureRandom("GDGMCCd2z5q7d".getBytes());
        System.out.println("class    :" + random.getClass());
        System.out.println("provider :" + random.getProvider());
        System.out.println("algorithm:" + random.getAlgorithm());
        System.out.println();

        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance(ALGORITHM_RSA, PROVIDER_BC);
        } catch (Exception e) {
            System.out.println("RSAUtils 密钥生成器实例化失败");
        }
        generator.initialize(KEYSIZE, random);
        keyPair = generator.generateKeyPair();

        byte[] privateEncoded = keyPair.getPrivate().getEncoded();
        System.out.println(Hex.toHexString(privateEncoded));

        byte[] publicEncoded = keyPair.getPublic().getEncoded();
        System.out.println(Hex.toHexString(publicEncoded));

        System.out.println();

        //在 SecureRandom 随机数的生成是固定的情况下， PRIVATE_KEY_BYTES 也就是可预测的了。
        if (Arrays.equals(PRIVATE_KEY_BYTES, privateEncoded)) {
            System.out.println("PRIVATE_KEY_BYTES == encoded");
        } else {
            System.out.println("PRIVATE_KEY_BYTES !!!!!= encoded");
        }
        System.out.println();

        //由于每次生成 RSA pub/pri 时使用的随机字节数量是固定的，
        //所以我们最后可以再次确认 SecureRandom 生成的随机字节是可预测的，他每次都是一样的
        byte[] someBytes = new byte[8];
        random.nextBytes(someBytes);
        System.out.println("some byte:" + Hex.toHexString(someBytes));
    }

}