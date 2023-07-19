package spring.jdbc.db.hsqldb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Configuration
public class HsqldbConfig {

    private DataSource recordDataSource;

    @Bean
    public DataSource dataSource() {
        // no need shutdown, EmbeddedDatabaseFactoryBean will take care of this
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder
                .setType(EmbeddedDatabaseType.HSQL) //.H2 or .DERBY
                .setName("hsqldb-embedded")
                .addScript("spring/jdbc/db/hsqldb/create-db.sql")
                .addScript("spring/jdbc/db/hsqldb/insert-data.sql")
                .setScriptEncoding(StandardCharsets.UTF_8.name())
                .build();

        recordDataSource = db;
        return db;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(@Autowired DataSource autowiredDataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(autowiredDataSource);
        DataSource jdbcTemplateDataSource = jdbcTemplate.getDataSource();

        // spring ioc 中的 bean 也可以用于参数注入，
        // 这里的 print 是为了验证通过参数注入的对象，是否和我们上面的创建的 recordDataSource 是同一个对象
        boolean autowiredDataSourceEquals = Objects.equals(recordDataSource, autowiredDataSource);
        boolean jdbcTemplateDataSourceEquals = Objects.equals(autowiredDataSource, jdbcTemplateDataSource);
        String format = String.format("autowiredDataSourceEquals=[%s], jdbcTemplateDataSourceEquals=[%s]", autowiredDataSourceEquals, jdbcTemplateDataSourceEquals);
        System.out.println(format);
        return jdbcTemplate;
    }

}

