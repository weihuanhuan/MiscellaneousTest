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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by JasonFitch on 9/28/2022.
 */
@WebServlet(urlPatterns = "/one-datasource-multiple-connection", name = "one-datasource-multiple-connection")
public class OneDatasourceMultipleConnectionTest extends HttpServlet {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final String globalName = "java:comp/env/jdbc/context-datasource-hsqldb";

    private final String amountParameterName = "amount";

    private final String timeoutParameterName = "timeout";

    private final String intervalParameterName = "interval";

    private DataSource datasource;

    /**
     * @see springmvc/src/main/resources/tomcat/context-datasource-hsqldb.xml
     */
    @Override
    public void init() throws ServletException {
        try {
            Context initContext = new InitialContext();
            datasource = (DataSource) initContext.lookup(globalName);
            logger.info(String.format("[%s] lookup result is [%s].", globalName, datasource == null ? "null" : datasource.toString()));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long amount = getLongParameter(req, amountParameterName, 5);
        long timeout = getLongParameter(req, timeoutParameterName, 500);
        long interval = getLongParameter(req, intervalParameterName, 100);

        try {
            List<Connection> connections = new ArrayList<>((int) amount);

            int index = (int) amount;
            while (index-- > 0) {
                sleep(interval);
                Connection connection = getConnection(datasource, index);
                connections.add(connection);
            }

            sleep(timeout);

            for (Connection connection : connections) {
                connection.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection(DataSource datasource, int index) throws SQLException {
        Objects.requireNonNull(datasource, "data source cannot be null!");

        Connection con = datasource.getConnection();
        logger.info(String.format("javax.sql.DataSource.getConnection() index [%s] result is [%s].", index, con == null ? "null" : con.toString()));
        return con;
    }

    private long getLongParameter(HttpServletRequest req, String parameterName, long defaultValue) {
        String parameterValueString = req.getParameter(parameterName);
        return parameterValueString == null ? defaultValue : Long.parseLong(parameterValueString);
    }

    private void sleep(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
