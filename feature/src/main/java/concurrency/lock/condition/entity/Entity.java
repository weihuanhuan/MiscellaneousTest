package concurrency.lock.condition.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class Entity {

    public static AtomicInteger createCount = new AtomicInteger(0);
    public static AtomicInteger processCount = new AtomicInteger(0);

    public Entity() {
        createCount.incrementAndGet();
    }

    public void use() {
        processCount.incrementAndGet();
    }

}
