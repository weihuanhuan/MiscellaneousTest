package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Created by JasonFitch on 7/2/2018.
 */
public class ServletTest extends HttpServlet
{
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try
        {
            Context initContext = new InitialContext();
//            Context envContext  = null;
//            envContext = (Context)initContext.lookup("java:/comp/env");
//            DataSource datasource = (DataSource)envContext.lookup("jdbc/Server");


            DataSource datasource = (DataSource)initContext.lookup("java:/comp/env/jdbc/Reference");

            Connection con = datasource.getConnection();
            if(null ==con)
            {
                System.out.println("null");

            }
            else
            {
                PrintWriter pw = resp.getWriter();
                pw.println(con.toString());
            }

        } catch (NamingException e)
        {
            e.printStackTrace();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }
}
