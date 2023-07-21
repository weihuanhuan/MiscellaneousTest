package spring.jdbc.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.jdbc.mybatis.bean.MybatisRedis;
import spring.jdbc.mybatis.service.RedisService;

import java.util.List;

@Controller
@RequestMapping("/mybatisRedis")
public class MybatisTransactionController {

    @Autowired
    private RedisService<MybatisRedis> redisService;

    @RequestMapping("/insert-fail-with-transactional")
    @ResponseBody
    @Transactional(rollbackFor = RuntimeException.class)
    public String mybatisRedisInsertFailWithTransactional() {
        System.out.println("\noldRedisList:");
        List<MybatisRedis> oldRedisList = redisService.listRedis();
        int oldSize = oldRedisList.size();

        System.out.println("\ninsertRedis:");
        MybatisRedis newMybatisRedis = redisService.insertRedis();

        System.out.println("\nnewRedisList:");
        List<MybatisRedis> newRedisList = redisService.listRedis();
        int newSize = newRedisList.size();

        String format = String.format("\noldSize=[%s], newSize=[%s].", oldSize, newSize);
        System.out.println(format);

        if (newSize != oldSize + 1) {
            throw new IllegalStateException("failed to insert newMybatisRedis=" + newMybatisRedis.getName());
        }
        throw new RuntimeException("try trigger rollback for insert fail!");
    }

    @RequestMapping("/insert-fail-without-transactional")
    @ResponseBody
    public void mybatisRedisInsertFailWithoutTransactional() {
        // 由于动态代理的问题，在实例内部直接调用 @Transactional 方法是不会触发事务处理的。
        mybatisRedisInsertFailWithTransactional();
    }

}
