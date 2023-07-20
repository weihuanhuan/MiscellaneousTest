package spring.jdbc.mybatis.config;

import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
// 需要指定相关的组件所在的包,否则这些组件不会被自动创建
@ComponentScan(basePackages = {"spring.jdbc.mybatis.service", "spring.jdbc.mybatis.controller"})
// NOTE <context:component-scan/> won't be able to scan and register mappers.
// Mappers are interfaces and, in order to register them to Spring,
// the scanner must know how to create a MapperFactoryBean for each interface it finds.
@MapperScan(basePackages = {"spring.jdbc.mybatis.mapper"})
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Autowired DataSource autowiredDataSource) throws Exception {
        System.out.println("autowiredDataSource=" + autowiredDataSource);

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(autowiredDataSource);

        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(@Autowired SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);

        // 我们没有指定 mybatis-config.xml 文件，可以使用代码的形式配置 mybatis
        org.apache.ibatis.session.Configuration configuration = sqlSessionTemplate.getConfiguration();
        configuration.setCacheEnabled(false);
        configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);

        // 这个 mapper 本来该在该文件中的 <mappers> 中进行注册 ，但是我们没有该文件，就需要使用代码的形式配置 mybatis 了。
        // 不过我们这里使用了 @MapperScan ，其可以自动的对 mapper 接口进行扫描，并自动的执行 addMapper 。
        // 另外在 spring ioc 也会自动的把  @MapperScan 扫描到的 mapper bean 注册到 ioc 中，
        // 所以我们也不需要手动的返回 RedisMapper bean ，这里仅仅是为了自定义 Configuration ，返回 SqlSessionTemplate 就行了
//        configuration.addMapper(RedisMapper.class);
//        RedisMapper mapper = sqlSessionTemplate.getMapper(RedisMapper.class);
        return sqlSessionTemplate;
    }

}
