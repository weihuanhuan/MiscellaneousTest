package Servlet.multidatasource;

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
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JasonFitch on 12/12/2022.
 */
@WebServlet(urlPatterns = "/multi-datasource-failover", name = "multi-datasource-failover")
public class MultiDatasourceConnectionTest extends HttpServlet {

    private static final Logger logger = Logger.getLogger(MultiDatasourceConnectionTest.class.getName());

    private static final String globalName = "jdbc/multi-datasource-failover";

    private static final String actionParameterName = "action";

    private static final String occupyAmountParameterName = "occupyAmount";
    private static final String occupyIntervalParameterName = "occupyInterval";

    private static final String releaseAmountParameterName = "releaseAmount";
    private static final String releaseIntervalParameterName = "releaseInterval";

    private static final String executeQueryParameterName = "executeQuery";

    private static final String sql = "select count(*) from t;";

    private DataSource datasource;
    private final List<Connection> connections = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        try {
            Context initContext = new InitialContext();
            datasource = (DataSource) initContext.lookup(globalName);

            String datasourceString = datasource == null ? "null" : datasource.toString();
            logger.info(String.format("[%s] lookup result is [%s].", globalName, datasourceString));
        } catch (NamingException e) {
            String format = String.format("failed to lookup [%s]!", globalName);
            RuntimeException runtimeException = new RuntimeException(format, e);
            logger.log(Level.WARNING, runtimeException.getMessage(), runtimeException);
            throw runtimeException;
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Objects.requireNonNull(datasource, "data source cannot be null!");

        String result;
        String action = getStringParameter(req, actionParameterName, "unknown");
        try {
            switch (action) {
                case "occupy":
                    result = occupyConnection(req);
                    break;
                case "release":
                    result = releaseConnection(req);
                    break;
                case "list":
                    result = listConnection(req);
                    break;
                case "execute":
                    result = execute(req);
                    break;
                case "unknown":
                default:
                    result = "unknown action!";
                    break;
            }
        } catch (SQLException e) {
            String format = String.format("failed to execute action [%s]!", action);
            RuntimeException runtimeException = new RuntimeException(format, e);
            logger.log(Level.WARNING, runtimeException.getMessage(), runtimeException);
            throw runtimeException;
        }

        String format = String.format("action\n[%s], \nresult\n[%s].", action, result);
        resp.getWriter().println(format);
    }

    private synchronized String occupyConnection(HttpServletRequest req) throws SQLException {
        long occupyAmount = getLongParameter(req, occupyAmountParameterName, 1);
        long occupyInterval = getLongParameter(req, occupyIntervalParameterName, 50);

        int index = (int) occupyAmount;
        while (index-- > 0) {
            sleep(occupyInterval);
            Connection connection = getConnection(req);
            connections.add(connection);
        }

        int afterOccupySize = connections.size();
        String format = String.format("occupyAmount [%s], occupyInterval [%s], afterOccupySize [%s].", occupyAmount, occupyInterval, afterOccupySize);
        return format;
    }

    private synchronized String releaseConnection(HttpServletRequest req) {
        long releaseAmount = getLongParameter(req, releaseAmountParameterName, 1);
        long releaseInterval = getLongParameter(req, releaseIntervalParameterName, 50);

        int occupiedAmount = connections.size();
        if (releaseAmount > occupiedAmount) {
            String format = String.format("releaseAmount [%s] greater than occupiedAmount [%s], just ignore it!", releaseAmount, occupiedAmount);
            return format;
        }

        int index = (int) releaseAmount;
        Iterator<Connection> iterator = connections.iterator();
        while (index-- > 0 && iterator.hasNext()) {
            sleep(releaseInterval);

            Connection connection = iterator.next();
            try {
                connection.close();
            } catch (SQLException e) {
                logger.info(String.format("failed to release connection [%s], just ignore it!", connection));
            }
            iterator.remove();
        }

        int afterReleaseSize = connections.size();
        String format = String.format("releaseAmount [%s], releaseInterval [%s], afterReleaseSize [%s].", releaseAmount, releaseInterval, afterReleaseSize);
        return format;
    }

    private synchronized String listConnection(HttpServletRequest req) throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();

        Iterator<Connection> iterator = connections.iterator();
        while (iterator.hasNext()) {
            Connection connection = iterator.next();

            try {
                String connectionStr = getConnectionString(connection);
                stringBuilder.append(connectionStr);
                stringBuilder.append("\n");
            } catch (SQLException e) {
                iterator.remove();
                logger.info(String.format("failed to list connection [%s], just remove it!", connection));
                throw e;
            }
        }

        int occupiedAmount = connections.size();
        String format = String.format("occupiedAmount [%s].", occupiedAmount);
        stringBuilder.append(format);
        return stringBuilder.toString();
    }

    private String execute(HttpServletRequest req) throws SQLException {
        //记得关闭查询测试所使用的连接，避免连接泄露
        //这里是逻辑关闭，由于连接池的作用，该连接的物理连接 tcp 还是和数据库保持着，真实的物理连接是否关闭则由连接池所管理
        try (Connection connection = getConnection(req);) {
            String queryString = executeQuery(req, connection);

            String connectionString = getConnectionString(connection);
            String format = String.format("connectionString\n[%s], \nqueryString\n[%s].", connectionString, queryString);
            return format;
        }
    }

    private Connection getConnection(HttpServletRequest req) throws SQLException {
        return datasource.getConnection();
    }

    private String getConnectionString(Connection connection) throws SQLException {
        if (connection == null) {
            return "null";
        }

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        if (databaseMetaData == null) {
            return connection.toString();
        }

        String url = databaseMetaData.getURL();
        if (url == null || url.isEmpty()) {
            return connection.toString();
        }
        return url;
    }

    private String executeQuery(HttpServletRequest req, Connection connection) throws SQLException {
        String executeQuery = getStringParameter(req, executeQueryParameterName, "true");

        boolean executeQueryBool = Boolean.parseBoolean(executeQuery);
        boolean executeResult = false;

        if (executeQueryBool) {
            //java.sql.Statement 也需要关闭
            try (Statement statement = connection.createStatement();) {
                executeResult = statement.execute(sql);
            }
        }

        String format = String.format("executeQuery [%s], executeResult [%s].", executeQueryBool, executeQueryBool ? executeResult : "no execute query");
        return format;
    }

    private String getStringParameter(HttpServletRequest req, String parameterName, String defaultValue) {
        String parameterValueString = req.getParameter(parameterName);
        return parameterValueString == null ? defaultValue : parameterValueString;
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
