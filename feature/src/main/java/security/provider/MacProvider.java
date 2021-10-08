package security.provider;

import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Set;

public class MacProvider {

    public static void main(String[] args) throws NoSuchAlgorithmException {

        String targetType = "Mac";

        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            System.out.println("###############################################");
            System.out.println(provider.getName());
            for (Provider.Service service : provider.getServices()) {
                String type = service.getType();
                if (!type.startsWith(targetType)) {
                    continue;
                }
                System.out.println("    " + type + " " + service.getAlgorithm());
            }
        }

        System.out.println("###############################################");
        Set<String> mac = Security.getAlgorithms("Mac");
        System.out.println(mac);

        System.out.println("###############################################");
        Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
        System.out.println(hmacSHA512);
    }

}
