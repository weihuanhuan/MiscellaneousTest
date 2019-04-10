package ThreadPoolExecutorTest.destory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 4/8/2019.
 */
public class DestroyableThreadPoolExecutor extends ThreadPoolExecutor {

    private boolean newThreadOnExecute = false;

    private boolean deadThreadOnAfterExecute = false;

    public DestroyableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public DestroyableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        System.out.println();
        System.out.println("beforeExecuteThreadID: " + Thread.currentThread().getId());
        super.beforeExecute(t, r);
    }

    @Override
    public void execute(Runnable command) {
        System.out.println("ExecuteThreadID: " + Thread.currentThread().getId());
        newThreadOnExecute = false;
        if (newThreadOnExecute) {
            try {
                Thread thread = new Thread(command);
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            super.execute(command);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        System.out.println("afterExecuteThreadID: " + Thread.currentThread().getId());
        super.afterExecute(r, t);
        deadThreadOnAfterExecute = false;
        if (deadThreadOnAfterExecute) {
            throw new ThreadNormalStopException();
        }
    }


}
