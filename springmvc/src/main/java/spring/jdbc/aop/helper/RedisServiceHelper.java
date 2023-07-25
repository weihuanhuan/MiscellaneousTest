package spring.jdbc.aop.helper;

import org.springframework.context.ApplicationContext;
import spring.jdbc.aop.service.ApplicationContextHolderService;
import spring.jdbc.mybatis.bean.MybatisRedis;
import spring.jdbc.mybatis.service.RedisService;

public class RedisServiceHelper {

    public static RedisService<MybatisRedis> getRedisService() {
        ApplicationContext applicationContext = ApplicationContextHolderService.getApplicationContext();

        RedisService<MybatisRedis> bean = applicationContext.getBean(RedisService.class);
        return bean;
    }

    public static MybatisRedis getRedisByName(String name) {
        RedisService<MybatisRedis> aopRedisService = getRedisService();

        MybatisRedis selectRedisByName = aopRedisService.selectRedisByName(name);
        return selectRedisByName;
    }

    public static int updateRedis(MybatisRedis mybatisRedis) {
        RedisService<MybatisRedis> aopRedisService = getRedisService();

        int i = aopRedisService.updateRedisByName(mybatisRedis);
        return i;
    }

}
