package Servlet;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by JasonFitch on 3/2/2020.
 */
@WebServlet(name = "LoggerRedirectTest", urlPatterns = "/loggerredirect")
public class LoggerRedirectTest extends HttpServlet {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestURI = req.getRequestURI();

        logger.info(requestURI + "+fromJDKLogger");
        System.out.println(requestURI + "+fromJDKSOUT");
        System.err.println(requestURI + "+fromJDKSERR");
    }
}
