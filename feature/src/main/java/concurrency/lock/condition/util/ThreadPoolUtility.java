package concurrency.lock.condition.util;

import concurrency.lock.condition.queue.EndlessBlockQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtility {

    public static void statisticTimeHook() {
        long startTime = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Total Running Time:" + TimeUnit.MILLISECONDS.toSeconds(duration));
        }));
    }

    public static void sleep(long timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException ignored) {
        }
    }

    public static ThreadPoolExecutor createEndlessThreadPoolExecutor(int poolSize, Runnable runnable, ThreadFactory threadFactory) {
        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
        return createEndlessThreadPoolExecutor(poolSize, runnable, threadFactory, handler);
    }

    public static ThreadPoolExecutor createEndlessThreadPoolExecutor(int poolSize, Runnable runnable, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        EndlessBlockQueue queue = new EndlessBlockQueue(runnable);

        ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor(poolSize, poolSize, Integer.MAX_VALUE, TimeUnit.SECONDS, queue, threadFactory, handler);
        // MUST BE prestartAllCoreThreads，因为没有人提交任务，所以第一个线程必须通过这个方式来手动触发运行
        threadPoolExecutor.prestartAllCoreThreads();
        threadPoolExecutor.allowCoreThreadTimeOut(false);
        return threadPoolExecutor;
    }

    public static ThreadPoolExecutor createNormalThreadPoolExecutor(int queueSize, int corePoolSize, int maximumPoolSize, String threadName, RejectedExecutionHandler handler) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadName, true);
        return createNormalThreadPoolExecutor(queueSize, corePoolSize, maximumPoolSize, threadFactory, handler);
    }

    public static ThreadPoolExecutor createNormalThreadPoolExecutor(int queueSize, int corePoolSize, int maximumPoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        return createThreadPoolExecutor(corePoolSize, maximumPoolSize, 5L, TimeUnit.SECONDS, queue, threadFactory, handler);
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, threadFactory, handler);
    }

    public static class DefaultThreadFactory implements ThreadFactory {

        private final AtomicInteger createCount = new AtomicInteger(0);

        private final String threadName;
        private final boolean daemon;

        public DefaultThreadFactory(String threadName, boolean daemon) {
            this.threadName = threadName;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            String nextThreadName = getNextThreadName();

            Thread thread = new Thread(r, nextThreadName);
            thread.setDaemon(daemon);
            return thread;
        }

        protected String getNextThreadName() {
            return threadName + "-" + createCount.incrementAndGet();
        }

    }

}
