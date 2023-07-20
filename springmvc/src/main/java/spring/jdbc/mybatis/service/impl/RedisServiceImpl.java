package spring.jdbc.mybatis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.jdbc.mybatis.bean.MybatisRedis;
import spring.jdbc.mybatis.mapper.RedisMapper;
import spring.jdbc.mybatis.service.RedisService;

import java.util.List;

@Service
public class RedisServiceImpl implements RedisService<MybatisRedis> {

    @Autowired
    private RedisMapper<MybatisRedis> redisMapper;

    @Override
    public List<MybatisRedis> listRedis() {
        List<MybatisRedis> listRedis = redisMapper.listRedis();
        listRedis.forEach(m -> System.out.println(m.getName()));
        return listRedis;
    }

    @Override
    public MybatisRedis insertRedis() {
        MybatisRedis selectRedisByName = redisMapper.selectRedisByName("default-redis-2");
        if (selectRedisByName == null) {
            throw new RuntimeException("failed to insert with selectRedisByName is null.");
        }

        MybatisRedis newMybatisRedis = new MybatisRedis();
        newMybatisRedis.copy(selectRedisByName);

        newMybatisRedis.setId(newMybatisRedis.getId() + 1);
        newMybatisRedis.setName("default-redis-3");

        int resultCount = redisMapper.insertRedis(newMybatisRedis);
        if (resultCount != 1) {
            throw new RuntimeException("failed to insert with resultCount=" + resultCount);
        }
        return newMybatisRedis;
    }

    @Override
    public MybatisRedis updateRedisByName() {
        MybatisRedis selectRedisByName = redisMapper.selectRedisByName("default-redis-3");
        if (selectRedisByName == null) {
            throw new RuntimeException("failed to update with selectRedisByName is null.");
        }

        selectRedisByName.setConfigText("update-config-3");

        int resultCount = redisMapper.updateRedisByName(selectRedisByName);
        if (resultCount != 1) {
            throw new RuntimeException("failed to update with resultCount=" + resultCount);
        }
        return selectRedisByName;
    }

    @Override
    public MybatisRedis deleteRedisByName() {
        MybatisRedis selectRedisByName = redisMapper.selectRedisByName("default-redis-3");
        if (selectRedisByName == null) {
            throw new RuntimeException("failed to delete with selectRedisByName is null.");
        }

        int resultCount = redisMapper.deleteRedisByName(selectRedisByName);
        if (resultCount != 1) {
            throw new RuntimeException("failed to delete with resultCount=" + resultCount);
        }
        return selectRedisByName;
    }

}
