package concurrency.lock.condition.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class EndlessBlockQueue extends ArrayBlockingQueue<Runnable> {

    private final Runnable runnable;

    public EndlessBlockQueue(Runnable runnable) {
        super(1);
        this.runnable = runnable;
    }

    @Override
    public Runnable take() throws InterruptedException {
        return runnable;
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        return runnable;
    }

    @Override
    public Runnable poll() {
        return runnable;
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
        if (runnable != this.runnable) {
            return false;
        }
        return true;
    }

    @Override
    public boolean offer(Runnable runnable) {
        if (runnable != this.runnable) {
            return false;
        }
        return true;
    }

    @Override
    public Runnable peek() {
        return runnable;
    }

}
