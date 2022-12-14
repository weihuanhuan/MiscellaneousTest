package database.hsqldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HSQLDBClientBlock {

    private static final String DRIVER_CLASSNAME = "org.hsqldb.jdbc.JDBCDriver";

    private static final String SQL = "select count(*) from t;";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        Class.forName(DRIVER_CLASSNAME);

        System.out.println("######################## isValidConnectionTest ########################");
        isValidConnectionTest();

    }

    private static void isValidConnectionTest() {
        String username = "SA";
        String password = "123456";

        String url1 = "jdbc:hsqldb:hsql://192.168.56.100:9001/multi-database-1;ifexists=true";
        isValidConnection(url1, username, password);

    }

    private static void isValidConnection(String url, String user, String password) {
        System.out.println();
        System.out.println("-----------" + url + "-----------");

        Connection connection = null;
        boolean valid;
        boolean execute = false;
        int count = -1;
        try {
            connection = DriverManager.getConnection(url, user, password);
            valid = connection.isValid(10);

            if (valid) {
                try (Statement statement = connection.createStatement();) {
                    execute = statement.execute(SQL);

                    try (ResultSet resultSet = statement.executeQuery(SQL);) {
                        //count function only return one row result
                        while (resultSet.next()) {
                            count = resultSet.getInt(1);
                        }
                    }
                }
            }

            String connectionString = getConnectionString(connection);
            String format = String.format("connection [$%s], valid [%s], execute [%s], count [%s]", connectionString, valid, execute, count);
            System.out.println(format);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getConnectionString(Connection connection) throws SQLException {
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

}
