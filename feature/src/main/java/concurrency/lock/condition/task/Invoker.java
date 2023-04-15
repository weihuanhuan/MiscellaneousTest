package concurrency.lock.condition.task;

import concurrency.lock.condition.role.SimplePool;
import concurrency.lock.condition.entity.Entity;

import java.util.concurrent.atomic.AtomicInteger;

public class Invoker extends BaseTask {

    public static AtomicInteger executeCount = new AtomicInteger(0);
    public static AtomicInteger processCount = new AtomicInteger(0);
    public static AtomicInteger retryCount = new AtomicInteger(0);

    public Invoker(SimplePool simplePool) {
        super(executeCount.incrementAndGet(), simplePool);
    }

    @Override
    public void run() {
        processCount.incrementAndGet();

        try {
            Entity entity = null;

            while (entity == null) {
                entity = simplePool.borrowEntity();

                if (entity == null) {
                    retryCount.incrementAndGet();
                }
            }

            entity.use();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}