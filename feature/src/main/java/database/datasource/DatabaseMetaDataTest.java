package database.datasource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseMetaDataTest {

    static String url = "url";
    static String username = "username";
    static String tableName = "tableName";

    public static void main(String[] args) throws SQLException {

        //使用 DatabaseMetaData 判断数据库是否存在某个表
        Connection connection = DriverManager.getConnection(url);
        DatabaseMetaData metaData = connection.getMetaData();
        String userName = metaData.getUserName();
        metaData.getTables(null, userName, tableName, new String[]{"Table"});
    }


}
