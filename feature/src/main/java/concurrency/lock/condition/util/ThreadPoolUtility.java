package concurrency.lock.condition.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ThreadPoolUtility {

    public static void sleep(long timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            String name = Thread.currentThread().getName();
            String format = String.format("interrupted:name=[%s], index=[%s], exception:%n%s", name, e);
            System.out.println(format);

            Thread.currentThread().interrupt();
        }
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(final int queueSize, final int corePoolSize, final int maximumPoolSize, final String threadName, ThreadFactory threadFactory, final RejectedExecutionHandler policy) {
        if (threadFactory == null) {
            threadFactory = new DefaultThreadFactory(threadName, true);
        }

        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 5L, SECONDS, queue, threadFactory, policy);
        return executor;
    }

    public static class DefaultThreadFactory implements ThreadFactory {

        public static AtomicInteger createCount = new AtomicInteger(0);

        private final String threadName;
        private final boolean daemon;

        public DefaultThreadFactory(String threadName, boolean daemon) {
            this.threadName = threadName;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, threadName);
            thread.setDaemon(daemon);

            createCount.incrementAndGet();
            return thread;
        }
    }

}
