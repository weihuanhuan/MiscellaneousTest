package spring.jdbc.transaction.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import spring.jdbc.mybatis.bean.MybatisRedis;

@Component
public class MybatisRedisEventListener {

    @EventListener(MybatisRedisInsertedEvent.class)
    public void onMybatisRedisInserted(MybatisRedisInsertedEvent mybatisRedisInsertedEvent) {
        MybatisRedis insertedMybatisRedis = mybatisRedisInsertedEvent.getInsertedMybatisRedis();
        if (insertedMybatisRedis == null) {
            throw new IllegalStateException("insertedMybatisRedis cannot be null!");
        }

        String name = insertedMybatisRedis.getName();
        System.out.println("@EventListener: insertedMybatisRedis=" + name);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void onMybatisRedisInsertedAfterCompletion(MybatisRedisInsertedEvent mybatisRedisInsertedEvent) {
        MybatisRedis insertedMybatisRedis = mybatisRedisInsertedEvent.getInsertedMybatisRedis();
        if (insertedMybatisRedis == null) {
            throw new IllegalStateException("insertedMybatisRedis cannot be null!");
        }

        String name = insertedMybatisRedis.getName();
        System.out.println("@TransactionalEventListener: insertedMybatisRedis=" + name);
    }

}
