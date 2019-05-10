package Servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by JasonFitch on 8/14/2018.
 */

@WebServlet(urlPatterns = "/file", name = "file")
public class FileTest extends HttpServlet
{
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        File file = new File("~/file");

        String fileFullPath = file.getCanonicalPath();
        System.out.println(fileFullPath);
//      C:\apache-tomcat-8.5.32\bin\~\file
//      /software/apache-tomcat-8.5.31/~/file


        PrintWriter pw = new PrintWriter(resp.getWriter());
        pw.println(fileFullPath);


    }
}
