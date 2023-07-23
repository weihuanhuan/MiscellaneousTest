package spring.jdbc.transaction.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"spring.jdbc.transaction.service", "spring.jdbc.transaction.controller"})
public class MybatisTransactionConfig {

    @Bean
    public PlatformTransactionManager platformTransactionManager(@Autowired DataSource transactionAutowiredDataSource) {
        System.out.println("transactionAutowiredDataSource=" + transactionAutowiredDataSource);

        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(transactionAutowiredDataSource);
        return dataSourceTransactionManager;
    }

}
