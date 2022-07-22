package crypto.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class ProviderServiceTest {

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        //更加详细的 jvm 信息用来确认具体的 jvm 版本，以及辨别具体的 jvm 实现厂商。
        String javaRuntimeVersion = System.getProperty("java.runtime.version");
        String javaVmVendor = System.getProperty("java.vm.vendor");
        String javaVmName = System.getProperty("java.vm.name");
        String javaVmInfoString = javaRuntimeVersion + "-" + javaVmVendor + "-" + javaVmName;

        String userDir = System.getProperty("user.dir");
        File fileName = new File(userDir, javaVmInfoString + ".log");
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        System.out.println(fileName.getAbsolutePath());
        System.out.println("################################################################################");

        Provider[] providers = Security.getProviders();
        int index = 0;
        for (Provider provider : providers) {
            System.out.println(String.format("%2d:%-20s %s", ++index, provider.getName(), provider.getVersion()));
        }

        System.out.println("################################################################################");

        for (Provider provider : providers) {
            String providerName = provider.getName();
            for (Provider.Service service : provider.getServices()) {
                String serviceType = service.getType();
                String serviceAlgorithm = service.getAlgorithm();
                ProviderServiceInfo providerServiceInfo = new ProviderServiceInfo(providerName, serviceType, serviceAlgorithm);
                fileOutputStream.write(providerServiceInfo.toString().getBytes(StandardCharsets.UTF_8));
                fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            }
        }

        //结果中虽然没有这个算法组合，但是返回的 AES 实现中，其算法实现本身是支持某些填充处理的，包括这里的 PKCS5Padding 填充算法。
        String algorithm = "AES/CFB/PKCS5Padding";
        Cipher instance = Cipher.getInstance(algorithm);
        System.out.println(instance);
        System.out.println(instance.getProvider());
        System.out.println(instance.getAlgorithm());
        System.out.println(instance.getBlockSize());
    }

}
