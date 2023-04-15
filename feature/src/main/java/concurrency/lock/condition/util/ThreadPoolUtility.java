package concurrency.lock.condition.util;

import concurrency.lock.condition.task.BaseTask;

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

    public static class Ignore implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof BaseTask) {
                BaseTask baseTask = (BaseTask) r;

                String simpleName = baseTask.getClass().getSimpleName();
                int index = baseTask.getIndex();
                String format = String.format("rejected:name=[%s], index=[%s].", simpleName, index);
                // 由于输出到 console 时会 lock stdout
                // 所以多线程运行时，其会大大的阻碍性能问题，建议不是 debug 问题时关掉输出
//                System.out.println(format);
            }
        }
    }

}
