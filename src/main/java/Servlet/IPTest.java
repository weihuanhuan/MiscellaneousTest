package Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by JasonFitch on 8/15/2018.
 */
@WebServlet(urlPatterns = "/ip", name = "ip")
public class IPTest extends HttpServlet
{
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Runtime rt = Runtime.getRuntime();
        try
        {
            InetAddress ipv6_local = Inet6Address.getByName("[fe80::20c:29ff:fe68:65ec]");
//            InetAddress ipv6_local = Inet6Address.getByName("[fe80::18c9:8e29:8d2c:acd3%7]");
//           这里不管指定不指定网卡序号，都可以建立正确的监听，地址和指定的一样，同时指定不存在的接口号会异常
//            这里结合军哥ipv6一文中，C编程必须指定接口序号，猜想是JDK帮我们做了这步？
//                  [fe80::18c9:8e29:8d2c:acd3%7]:65010
            ServerSocket ss_local = new ServerSocket(65010, 10, ipv6_local);

            Process p = rt.exec("netstat -ano");

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.contains("65001") || line.contains("65010"))
                {
                    System.out.println(line);
                }
            }

            Socket client_local = ss_local.accept();

            if (null != client_local)
                System.out.println(client_local.toString());

            OutputStream os = client_local.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            pw.println(ss_local.getLocalSocketAddress().toString()+"\nByServlet");
            pw.flush();

            client_local.close();
            ss_local.close();


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
