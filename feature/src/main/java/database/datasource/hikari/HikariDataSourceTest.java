package database.datasource.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariDataSourceTest {

    static {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String url = "jdbc:hsqldb:hsql://192.168.56.100:9001/timeout-database-1;ifexists=true";
        String username = "SA";
        String password = "123456";

        System.out.println("######################### createHikariDataSource #########################");
        HikariDataSource hikariDataSource = createHikariDataSource(url, username, password);
        System.out.println(hikariDataSource);

        System.out.println("######################### com.zaxxer.hikari.HikariDataSource.getConnection() #########################");
        Connection connection = hikariDataSource.getConnection();
        System.out.println(connection);
    }

    public static HikariDataSource createHikariDataSource(String url, String username, String password) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);

        hikariConfig.setInitializationFailTimeout(-1);

        hikariConfig.setConnectionTimeout(3000);
        hikariConfig.setValidationTimeout(5000);

        // https://github.com/brettwooldridge/HikariCP
        // 1 MySQL-specific example, DO NOT COPY VERBATIM.
//        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
//        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
//        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return hikariDataSource;
    }

}
