package spring.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by JasonFitch on 6/5/2019.
 */

@RestController
public class JDBCMultiDataSourceTest {

//    @Resource
    private JdbcTemplate jdbcTemplate;

    @GetMapping("multijdbc")
    public String getMutliJDBC() {

        String info = "BLANK";

        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource != null) {

            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                info = connection.toString();
                System.out.println(info);
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            info = "dataSource == null";
            System.out.println(info);
        }

        return info;
    }


}
