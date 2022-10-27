package network.ipv6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Created by JasonFitch on 8/13/2018.
 */
public class Server
{
    public static void main(String[] args)
    {

        ClientBuilder builder = ClientBuilder.newBuilder();
        Client jerseyClient = builder.build();

        WebTarget wt = jerseyClient.target("http://2001:db8:0:f101::1:6900/rest");
        //需要加上[]否则回出现异常 java.net.MalformedURLException

        Runtime rt = Runtime.getRuntime();
        try
        {
            InetAddress ipv6 = Inet6Address.getByName("::");
            //绑定地址 [::] 或者是 0.0.0.0 将会同时监听 IPv4 和 IPv6
//            同时 JDK对于 IPv6 是不是用 [ ] 包裹都可以准确的识别。
            ServerSocket ss = new ServerSocket(65001, 10, ipv6);

            InetAddress ipv6_local = Inet6Address.getByName("[fe80::18c9:8e29:8d2c:acd3]");
//            InetAddress ipv6_local = Inet6Address.getByName("[fe80::18c9:8e29:8d2c:acd3%7]");
//           这里不管指定不指定网卡序号，都可以建立正确的监听，且监听的地址和在指定接口号是一样的，即JDK会判断接口号
//           同时指定不存在的接口号会异常
//                  [fe80::18c9:8e29:8d2c:acd3%7]:65010
            ServerSocket ss_local = new ServerSocket(65010, 10, ipv6_local);

            Process p = rt.exec("netstat -ano");

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null)
                if (line.contains("65001") || line.contains("65010"))
                    System.out.println(line);

            while (true)
            {
                Socket client_local = ss_local.accept();

                if (null != client_local)
                    System.out.println(client_local.toString());

                OutputStream os = client_local.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                pw.println(ss_local.getLocalSocketAddress().toString() + "\nByApplication");
                pw.flush();
                client_local.close();
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
