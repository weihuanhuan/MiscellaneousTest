package spring.jdbc;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;

public class OracleJDBCTest {

    public static void main(String[] args) {

        try {

            OracleDataSource oracleDataSource = new OracleDataSource();

//            OracleDataSource.urlExplicit
//            OracleDataSource.makeURL()
            oracleDataSource.setURL("URL");
            oracleDataSource.getURL();

            oracleDataSource.setUser("user");

            // oracle 要设置驱动类型，如果不显示设置url，否则会有异常  OracleDataSource.makeURL
            oracleDataSource.setDriverType("thin");

            //JF Oracle 的驱动url 属性的设置仅仅是大写的，而mysql是大小写都适配了的
            //   双方都是在设置了url后将是的explicitURL置为true使用显示设置的，否则按照相关成员属性构造一个url使用
            MysqlDataSource mysqlDataSource = new MysqlDataSource();

//            MysqlDataSource.explicitUrl
//            String builtUrl = "jdbc:mysql://";
//            builtUrl = builtUrl + getServerName() + ":" + getPort() + "/" + getDatabaseName();
            mysqlDataSource.setURL("URL");
            mysqlDataSource.getURL();

            mysqlDataSource.setUrl("url");
            mysqlDataSource.getUrl();

            mysqlDataSource.setUser("user");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
