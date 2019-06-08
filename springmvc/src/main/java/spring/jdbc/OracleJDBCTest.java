package spring.jdbc;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.SQLException;

public class OracleJDBCTest {


    public static void main(String[] args) {

        OracleDataSource oracleDataSource = null;
        try {
            oracleDataSource = new OracleDataSource();
            //JF Oracle 的驱动url 属性的设置仅仅是大写的，而mysql是大小写都适配了的
            oracleDataSource.setURL("url");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
