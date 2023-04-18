package concurrency.lock.condition.multiple.manager;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedLockConditionThreadFactory implements ThreadFactory {

    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        String name = "monitor-" + counter.incrementAndGet();

        //另外 ThreadPoolExecutor 自身的 java.util.concurrent.ThreadPoolExecutor.Worker.run 方法才会自动的在线程宕机时，进行补充线程
        //所以如果我们这里替换成了我们的 Waiter ，那么线程宕机后，就不会补充线程了。
        //但是 jdk 的 java.util.concurrent.ThreadPoolExecutor.addWorker 需要保证 queue 不是空才会添加线程，
        //另外在其 java.util.concurrent.ThreadPoolExecutor.getTask 时又需要从队列中获取用户的 Runnable ，这里即我们的 Waiter
        //所以这里如果没有人向线程池的队列中提交任务就会导致，不会有新的线程被调度执行了。
        Monitor monitor = new Monitor(r, name);
        monitor.setDaemon(true);
        return monitor;
    }

    private static class Monitor extends Thread {

        public Monitor(Runnable target, String name) {
            super(target, name);
        }

        @Override
        public void run() {
            //这里的 super.run 调用的不是我们的 Waiter ，而是 jdk 的java.util.concurrent.ThreadPoolExecutor.Worker.run
            //其内部会使用 java.util.concurrent.ThreadPoolExecutor.getTask 来获取任务，而由于我们并没有添加任务，所以就会一直 await 了。
            //因此对于 Waiter 的循环包装调用，应该放到 concurrency.lock.condition.multiple.SharedWorkManager.Waiter.run 中。
//            do {
            super.run();
//            } while (!Thread.currentThread().isInterrupted());
        }

    }

}
