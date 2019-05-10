package ThreadPoolExecutorTest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JasonFitch on 3/13/2019.
 */
public class CustomThreadPoolExecutor extends ThreadPoolExecutor {

    private AtomicInteger skipCount = new AtomicInteger(0);

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.setRejectedExecutionHandler(new CustomRejectedExecutionHandler());
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(task);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
//        System.out.println("beforeExecute:" + r.toString());
        super.beforeExecute(t, r);
    }

    @Override
    public void execute(Runnable command) {
//        System.out.println("execute:" + command.toString());
        super.execute(command);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
//        System.out.println("afterExecute:" + r.toString());
        super.afterExecute(r, t);
    }

    @Override
    public String toString() {
        return super.toString() + " [skip tasks = " + skipCount.get() + "]";
    }

    private class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println(executor.toString());
            System.exit(1);
        }
    }


}
