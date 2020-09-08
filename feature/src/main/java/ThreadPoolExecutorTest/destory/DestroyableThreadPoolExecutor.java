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

    private boolean deadThreadOnAfterExecute = true;

    public DestroyableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public DestroyableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        System.out.println();
        System.out.println("beforeExecuteThreadName: " + Thread.currentThread().getName());
        super.beforeExecute(t, r);
    }

    @Override
    public void execute(Runnable command) {
        System.out.println("ExecuteThreadName: " + Thread.currentThread().getName());
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
        System.out.println("afterExecuteThreadName: " + Thread.currentThread().getName());
        super.afterExecute(r, t);

        //JF 线程的终止
        //在 ThreadPoolExecutor中，Worker 是一个由 Thread 来执行的 Runnable
        //启动这个 Thread 时 ThreadPoolExecutor.Worker.run 方法会调用 ThreadPoolExecutor.runWorker，

        //而在 runWorker 中存在一个不断执行的循环，
        //他循环调用 ThreadPoolExecutor.getTask 来的获取任务，并执行 before execute after 这些阶段的处理
        //同时执行这些 Worker 时所用的线程是都来自 ThreadFactory.newThread 的

        //所以为了保证这些线程在任务执行之后就推出，我们可以在 after 阶段 丢出一个异常使得 Worker 从循环脱离
        if (deadThreadOnAfterExecute) {
            throw new ThreadNormalStopException();
        }
    }

}
