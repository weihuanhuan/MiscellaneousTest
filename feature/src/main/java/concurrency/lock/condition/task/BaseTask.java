package concurrency.lock.condition.task;

import concurrency.lock.condition.role.SimplePool;

public abstract class BaseTask implements Runnable {

    private final int index;

    protected final SimplePool simplePool;

    public BaseTask(int index, SimplePool simplePool) {
        this.index = index;
        this.simplePool = simplePool;
    }

    public int getIndex() {
        return index;
    }

}
