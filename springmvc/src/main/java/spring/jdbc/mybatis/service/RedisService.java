package spring.jdbc.mybatis.service;

import spring.jdbc.mybatis.bean.MybatisRedis;

import java.util.List;

public interface RedisService<T extends MybatisRedis> {

    List<T> listRedis();

    T insertRedis();

    T updateRedisByName();

    T deleteRedisByName();

    T selectRedisByName(String name);

    int updateRedisByName(T mybatisRedis);

}
