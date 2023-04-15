package concurrency.lock.condition.task;

import concurrency.lock.condition.role.SimplePool;

import java.util.concurrent.TimeUnit;

public abstract class BaseTask implements Runnable {

    private final int index;

    protected final SimplePool simplePool;

    public BaseTask(int index, SimplePool simplePool) {
        this.index = index;
        this.simplePool = simplePool;
    }

    protected void sleep(long timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            String simpleName = this.getClass().getSimpleName();
            String format = String.format("interrupted:name=[%s], index=[%s], exception:%n%s", simpleName, index, e);
            System.out.println(format);

            Thread.currentThread().interrupt();
        }
    }

    public int getIndex() {
        return index;
    }

}
