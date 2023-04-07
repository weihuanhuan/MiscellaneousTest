package database.oceanbase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class OceanbaseMain {

    public static void main(String[] args) {

        try {
            String url = "jdbc:oceanbase://47.98.139.203:2881/testdb?pool=false";
            String user = "root@obbmysql";

            url = "jdbc:oceanbase://47.98.139.203:2883/testdb?pool=false";
            user = "root@obbmysql#obdemo";

            String password = "123456mysql";
            Class.forName("com.alipay.oceanbase.jdbc.Driver");
            //这个驱动也可以使用，是 OceanBase Connector/J V1 版本的驱动
            //Class.forName("com.alipay.oceanbase.obproxy.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println(connection);

            Statement st = connection.createStatement();
            boolean execute = st.execute("select 1 from dual;");
            System.out.println(execute);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

}
