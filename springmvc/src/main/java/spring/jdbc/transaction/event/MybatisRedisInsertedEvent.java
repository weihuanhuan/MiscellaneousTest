package spring.jdbc.transaction.event;


import spring.jdbc.mybatis.bean.MybatisRedis;

public class MybatisRedisInsertedEvent {

    private final MybatisRedis insertedMybatisRedis;

    public MybatisRedisInsertedEvent(MybatisRedis insertedMybatisRedis) {
        this.insertedMybatisRedis = insertedMybatisRedis;
    }

    public MybatisRedis getInsertedMybatisRedis() {
        return insertedMybatisRedis;
    }

}
