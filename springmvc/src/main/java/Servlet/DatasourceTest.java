package Servlet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by JasonFitch on 7/2/2018.
 */
@WebServlet(urlPatterns = "/datasource", name = "datasource")
public class DatasourceTest extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String prefix = "java:comp/env/";

        String globalName = "jdbc/Server";
        lookupTest(prefix + globalName);

        String contextName = "jdbc/Context";
        lookupTest(prefix + contextName);

        String linkName = "jdbc/Reference";
        lookupTest(prefix + linkName);

        String factoryName = "jdbc/Hikari";
        lookupTest(prefix + factoryName);

        //这里我们去掉了前缀，试图查找全局的资源。
        lookupTest(globalName);

        lookupTest(contextName);

        lookupTest(linkName);

        lookupTest(factoryName);
    }

    private void lookupTest(String name) {
        try {
            Context initContext = new InitialContext();
            DataSource datasource = (DataSource) initContext.lookup(name);

            Connection con = datasource.getConnection();
            System.out.println(String.format("[%s] lookup result is [%s].", name, con == null ? "null" : con.toString()));
        } catch (NamingException | SQLException e) {
            System.out.println(String.format("[%s] lookup exception is:", name));
            System.out.println(exceptionToString(e));
        }
    }

    private String exceptionToString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        e.printStackTrace(printWriter);
        printWriter.flush();

        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }

}
