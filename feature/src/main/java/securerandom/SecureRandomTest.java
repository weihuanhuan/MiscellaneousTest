package securerandom;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JasonFitch on 9/9/2019.
 */
public class SecureRandomTest {

    static {
        String javahome = System.getProperty("java.home");
        System.out.println(javahome);

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 0);

        //查看当前系统的provider
        Provider[] providers = Security.getProviders();
        List<Provider> providerList = Arrays.asList(providers);
        for (Provider provider : providerList) {
            System.out.println(provider);
        }
        System.out.println();

        // 对于安全随机数的来源有如下俩个属性可以配置，其中 系统属性的优先级高于安全属性的，
        // 同时要注意在win和linux上他们对于不同PRNG算法的影响

        //安全随机数的来源，java.security 配置
        String source = Security.getProperty("securerandom.source");
        System.out.println(source);

        //安全随机数来源， system properties 配置
        String egd = System.getProperty("java.security.egd");
        System.out.println(egd);

    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

//        -Djava.security.egd=file:/dev/./urandom
//        就是使用一个不阻塞的随机数，注意其中路径中的 . 一个bug jdk8修复了。
//        这个随机数将SECURERANDOM的算法切换到 SHA1PRNG 上边，这个算法每次的结果才是一样的，
//        win默认的算法是 SHA1PRNG, 这个算法利用 hash 运行从 seed 中生成随机数，所以种子确定，随机数确定
//        linux默认的算法是 NativePRNG，这个算法每次从 [u]random设备中取随机数，所以结果是会变得

        SecureRandom secureRandom = new SecureRandom("GDGMCCd2z5q7d".getBytes());
//        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        System.out.println();
        System.out.println("class    :" + secureRandom.getClass());
        System.out.println("provider :" + secureRandom.getProvider());
        System.out.println("algorithm:" + secureRandom.getAlgorithm());

        System.out.println();
        byte[] bytes = new byte[1];
        for (int i = 0; i < 10; ++i) {
            secureRandom.nextBytes(bytes);
            String s = Arrays.toString(bytes);
            System.out.print(s);
        }
        System.out.println();

        //使用相同的种子后每次运行的结果都是相同的，可用来生成固定的随机数序列，但是要保证使用者的取得顺序是一样的。
        //[-69][-23][112][-94][-74][85][-79][-42][116][9]
        //[-69][-23][112][-94][-74][85][-79][-42][116][9]

    }

}

