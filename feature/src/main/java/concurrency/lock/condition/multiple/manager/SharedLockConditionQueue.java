package concurrency.lock.condition.multiple.manager;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SharedLockConditionQueue extends LinkedBlockingQueue<Runnable> {

    private final Runnable waiter;

    public SharedLockConditionQueue(Runnable waiter) {
        this.waiter = waiter;
    }

    @Override
    public Runnable take() throws InterruptedException {
        return waiter;
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        return waiter;
    }

    @Override
    public Runnable poll() {
        return waiter;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void put(Runnable runnable) throws InterruptedException {
        //same as put successfully
    }

    @Override
    public boolean offer(Runnable runnable, long timeout, TimeUnit unit) throws InterruptedException {
        if (runnable != waiter) {
            return false;
        }
        return true;
    }

    @Override
    public boolean offer(Runnable runnable) {
        if (runnable != waiter) {
            return false;
        }
        return true;
    }

    @Override
    public Runnable peek() {
        return waiter;
    }

}
