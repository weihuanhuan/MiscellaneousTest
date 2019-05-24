package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by JasonFitch on 5/23/2019.
 */
//JF servlet 版本问题
// 如使用 WebServlet注解 后却访问不到这个 servlet，是因为这个注解是servlet3.0中才引入的.
// 所以原因可能是
//     1.服务器不支持 servlet3.0
//     2.应用的web.xml 中指定的应用版本是 servlet3.0 以下的版本
// 注意某些服务器，比如tomcat8.5.37，而glassfish则不会这样子处理。
// 即使web.xml中指定是servlet2.5版本，但是当应用使用 WebServlet后他依旧会扫描改注解，并实例化该servlet
@WebServlet(name = "ServerNameTest", urlPatterns = "/servername")
public class ServerNameTest extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            PrintWriter pw = resp.getWriter();

            String serverName = req.getServerName();
            pw.println("serverName");
            pw.println(serverName);

            String virtualServerName = req.getServletContext().getVirtualServerName();
            pw.println("virtualServerName");
            pw.println(virtualServerName);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }
}
