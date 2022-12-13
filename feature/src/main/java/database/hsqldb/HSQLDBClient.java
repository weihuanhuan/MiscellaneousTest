package database.hsqldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HSQLDBClient {

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

        // http://www.hsqldb.org/doc/2.0/guide/running-chapt.html
        // This feature has a side effect that can confuse new users.
        // If a mistake is made in specifying the path for connecting to an existing database,
        // a connection is nevertheless established to a new database. For troubleshooting purposes,
        // you can specify a connection property ifexists=true to allow connection to an existing database only and avoid creating a new database.
        // In this case, if the database does not exist, the getConnection() method will throw an exception.
        String url1 = "jdbc:hsqldb:hsql://192.168.56.100:9001/multi-database-1;ifexists=true";
        isValidConnection(url1, username, password);

        String url2 = "jdbc:hsqldb:jdbc:hsqldb:file:dbdir/db2/multi-database-2;ifexists=true";
        isValidConnection(url2, username, password);

        String url3 = "jdbc:hsqldb:mem:dbdir/db3/multi-database-3;ifexists=true";
        isValidConnection(url3, username, password);
    }

    private static void isValidConnection(String url, String user, String password) {
        System.out.println();
        System.out.println("-----------" + url + "-----------");

        Connection connection = null;
        boolean valid;
        boolean execute = false;
        int count = -1;
        try {
            //注意关闭 java.sql.Connection 和 java.sql.Statement 和 java.sql.ResultSet
            //这里是物理关闭，由于并没有使用池化技术，该连接的物理 tcp 连接也会直接与数据库断开
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
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
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

        //通过 java.sql.DatabaseMetaData.getURL 标识所连接的数据库是哪一个
        String url = databaseMetaData.getURL();
        if (url == null || url.isEmpty()) {
            return connection.toString();
        }
        return url;
    }

}
