package database.mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlClient {

    private static final String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    private static final String SQL = "select 1;";

    static {
        try {
            Class.forName(DRIVER_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        System.out.println("######################## timeoutTest ########################");
        timeoutTest();

    }

    private static void timeoutTest() {
        String username = "root";
        String password = "123456";

        String url = "jdbc:mysql://192.168.56.10:3306/testdb";
        queryWithTimeout(url, username, password);
    }

    private static void queryWithTimeout(String url, String user, String password) {
        System.out.println("-----------" + url + "-----------");

        DriverManager.setLoginTimeout(8);

        long connectStart = System.currentTimeMillis();

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);

            connection.setNetworkTimeout(Runnable::run, 16 * 1000);
            waitInput();
            long queryStart = System.currentTimeMillis();

            try (Statement statement = connection.createStatement();) {
                statement.setQueryTimeout(12);

                try (ResultSet resultSet = statement.executeQuery(SQL)) {
                    while (resultSet.next()) {
                        resultSet.getInt(1);
                    }
                }
            } finally {
                long queryEnd = System.currentTimeMillis();
                System.out.println("query duration=" + (queryEnd - queryStart));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long connectEnd = System.currentTimeMillis();
            System.out.println("connect duration=" + (connectEnd - connectStart));

            silentClose(connection);
        }
    }

    public static void waitInput() throws IOException {
        System.out.println("System.in.read()");
        int read = System.in.read();
        System.out.println("read=" + read);
    }

    public static void silentClose(AutoCloseable autoCloseable) {
        try {
            if (autoCloseable != null) {
                autoCloseable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
