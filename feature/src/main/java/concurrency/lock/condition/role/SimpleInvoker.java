package concurrency.lock.condition.role;

import concurrency.lock.condition.task.Invoker;
import concurrency.lock.condition.util.ThreadPoolUtility;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleInvoker {

    // invoker config
    private final int queueLength;
    private final int maxInvokerThread;

    // invoker executor
    private ThreadPoolExecutor invokerThreadPoolExecutor;

    // test pool
    private final SimplePool simplePool;

    public SimpleInvoker(SimplePool simplePool, int queueLength, int maxInvokerThread) {
        this.queueLength = queueLength;
        this.maxInvokerThread = maxInvokerThread;

        this.simplePool = simplePool;

        initInvoker();
    }

    private void initInvoker() {
        RejectedExecutionHandler handler = new ThreadPoolUtility.Ignore();
        invokerThreadPoolExecutor = ThreadPoolUtility.createThreadPoolExecutor(queueLength, 1, maxInvokerThread, "invoker", null, handler);
    }

    // **************** call by invoker ****************
    public void addInvokerWithInterval(long timeout, TimeUnit timeUnit) {
        Thread addInvokerThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    Invoker invoker = new Invoker(simplePool);
                    invokerThreadPoolExecutor.execute(invoker);

                    if (timeout > 0) {
                        ThreadPoolUtility.sleep(timeout, timeUnit);
                    }
                }
            }
        };

        addInvokerThread.setDaemon(true);
        addInvokerThread.start();
    }

    public void shutdown(long timeout, TimeUnit timeUnit) throws InterruptedException {
        invokerThreadPoolExecutor.shutdown();
        invokerThreadPoolExecutor.awaitTermination(timeout, timeUnit);
    }

}
