package spring.jdbc.aop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.jdbc.aop.helper.RedisServiceHelper;
import spring.jdbc.mybatis.bean.MybatisRedis;

@Service
public class AopTransactionService {

    @Transactional(rollbackFor = RuntimeException.class)
    public String updateSuccessWithTransactional() {
        String updatedName = "default-redis-2";

        System.out.println("\noldRedisByName");
        MybatisRedis oldRedisByName = RedisServiceHelper.getRedisByName(updatedName);

        oldRedisByName.setConfigText("update-config-2");

        System.out.println("updateRedis");
        int i = RedisServiceHelper.updateRedis(oldRedisByName);
        if (i != 1) {
            String format = String.format("name=[%s], excepted=[%s], actual=[%s]", updatedName, 1, i);
            throw new RuntimeException(format);
        }

        System.out.println("newRedisByName\n");
        MybatisRedis newRedisByName = RedisServiceHelper.getRedisByName(updatedName);
        return newRedisByName.getConfigText();
    }

}
