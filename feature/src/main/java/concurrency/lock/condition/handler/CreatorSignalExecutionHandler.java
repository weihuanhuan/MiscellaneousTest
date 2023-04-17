package concurrency.lock.condition.handler;

import concurrency.lock.condition.role.SimplePool;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class CreatorSignalExecutionHandler implements RejectedExecutionHandler {

    private final SimplePool simplePool;

    public CreatorSignalExecutionHandler(SimplePool simplePool) {
        this.simplePool = simplePool;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        simplePool.trySignalCreate();
    }

}
