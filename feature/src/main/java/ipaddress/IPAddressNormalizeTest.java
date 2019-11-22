package ipaddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by JasonFitch on 11/13/2019.
 */
public class IPAddressNormalizeTest {


    public static void main(String[] args) throws UnknownHostException {

        normalizeIPAddressTest();
        regularExpressionIPAddressTest();

    }

    public static void regularExpressionIPAddressTest() {
        System.out.println("------------------------------");
        String IPv4_NUMBER = "(\\d{1,2}|[0-1]\\d{2}|2[0-4]\\d|25[0-5])";
        String IPv4_REGEX = IPv4_NUMBER + "(\\." + IPv4_NUMBER + "){3}";
        String IPv4_COMMA_REGEX = IPv4_REGEX + "(," + IPv4_REGEX + ")*";

        String IPv4Address = "127.10.10.11";
        String IPv4AddressList = "192.168.9.177,192.168.9.177,192.168.9.177";

        boolean matchesIPv4 = IPv4Address.matches(IPv4_REGEX);
        System.out.println(matchesIPv4);

        boolean matchesIPv4_Comma = IPv4AddressList.matches(IPv4_COMMA_REGEX);
        System.out.println(matchesIPv4_Comma);
    }


    public static void normalizeIPAddressTest() {
        System.out.println("------------------------------");
        printIP("0134.148.0.1");
        printIP("134.0148.0.010");
        printIP("0134.0148.0.010");

        System.out.println("------------------------------");
        printIP("2001:db8:0:0:1:0:0:1");
        printIP("2001:db8::1:0:0:1");
        printIP("2001:db8:0:0:1::1");

        System.out.println("------------------------------");

//JF      InetAddress.getByName(s).getHostAddress() 会将各种奇怪形式的 合法IP地址, 转换为 标准IP地址，
//JF      对于主机名的形式，调用 InetAddress.getByName 会进行解析其IP地址，无法解析会抛出异常
//        printIP("cestos19");
    }

    public static void printIP(String ipStr) {
        String normalize;
        try {
            normalize = normalize(ipStr);
            System.out.println(normalize);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("error original:" + ipStr);
        }
    }

    public static String normalize(String s) throws UnknownHostException {
        return InetAddress.getByName(s).getHostAddress();
    }
}



