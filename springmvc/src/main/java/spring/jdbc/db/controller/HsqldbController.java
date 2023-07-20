package spring.jdbc.db.controller;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

@Controller
public class HsqldbController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/hsqldb-query-all")
    @ResponseBody
    public String hsqldbQueryAll() {
        Integer execute = jdbcTemplate.execute(new StatementCallback<Integer>() {
            @Override
            public Integer doInStatement(Statement stmt) throws SQLException, DataAccessException {
                int counter = 0;
                ResultSet resultSet = stmt.executeQuery("select * from redis;");

                while (resultSet.next()) {
                    ++counter;
                    String name = resultSet.getString(2);

                    String format = String.format("counter=[%s], name=[%s].", counter, name);
                    System.out.println(format);
                }
                return counter;
            }
        });
        return String.valueOf(execute);
    }

    @RequestMapping("/hsqldb-manager-swing")
    public void hsqldbManagerSwing() {
        Connection connection = null;
        try {
            DataSource dataSource = jdbcTemplate.getDataSource();

            // query hsqldb connection info
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            String url = metaData.getURL();
            String userName = metaData.getUserName();

            // using hsqldb url and username
            DatabaseManagerSwing.main(new String[]{"--url", url, "--user", userName, "--password", ""});

            // keep hsqldb manager swing running
            TimeUnit.SECONDS.sleep(60 * 60 * 24);
        } catch (InterruptedException | SQLException exception) {
            exception.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
