package ipv4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by JasonFitch on 8/16/2018.
 */
public class TCPClientWithoutHttpVersion {

    public static void main(String[] args) {

        try {

            // tomcat
            Socket sc = new Socket("127.0.0.1", 8080);
            sc.getOutputStream().write("GET /springmvc/servername".getBytes());

            // glassfish
//            Socket sc = new Socket("127.0.0.1", 8000);
//            sc.getOutputStream().write("GET /springmvc/servername HTTP/3.3".getBytes());

            // weblogic
//            Socket sc = new Socket("169.254.126.174", 7001);
//            sc.getOutputStream().write("GET /springmvc/servername HTTP/3.3".getBytes());

            // request line end
            sc.getOutputStream().write("\r\n".getBytes());

            // host header
//            sc.getOutputStream().write("Host:localhost".getBytes());
//            sc.getOutputStream().write("\r\n".getBytes());

            // request blank line
            sc.getOutputStream().write("\r\n".getBytes());

            InputStream is = sc.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            System.out.println("###########################################################");
            while (null != (line = br.readLine())) {
                System.out.println(line);
            }

            System.out.println("###########################################################");
            InetAddress localAddress = sc.getLocalAddress();
            //JF hostname
            // 触发 localAddress 中 hostName 字段的填充，否则默认是没有该值的。
            // 对于不同的IP,其所对应的 hostname 可能是不同的。
            String hostName = localAddress.getHostName();

            //这里的hostname来源是，对应ip在host文件中的第一条记录，如果hosts中没找见的话，则使用操作系统的hostname值。
            System.out.println(localAddress);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
