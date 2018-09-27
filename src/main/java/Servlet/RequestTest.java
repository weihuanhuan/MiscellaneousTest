package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by JasonFitch on 7/24/2018.
 */
public class RequestTest extends HttpServlet
{
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        System.out.println("This sentence from Servlet \"RequestTest\" by using a System.out.println()");
//          默认情况下 仅仅是 输出到 tomcat 的 console 中，在日志文件中没有这个,页面也没有这个。
        PrintWriter pw = resp.getWriter();
        pw.println("Request Succeed");
    }
}
