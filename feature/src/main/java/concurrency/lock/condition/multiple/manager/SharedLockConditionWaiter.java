package concurrency.lock.condition.multiple.manager;

import concurrency.lock.condition.queue.NoticableLinkedBlockingDeque;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SharedLockConditionWaiter<E> implements Runnable {

    private final SharedLockConditionManager<E> manager;

    private final NoticableLinkedBlockingDeque<E> queue;
    private final AtomicBoolean hasWork;

    public SharedLockConditionWaiter(SharedLockConditionManager<E> manager, NoticableLinkedBlockingDeque<E> queue, AtomicBoolean hasWork) {
        this.manager = manager;
        this.queue = queue;
        this.hasWork = hasWork;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();

        int signalAllIterators = 0;

        do {
            try {
                while (!queue.hasTakeWaiters()) {
                    hasWork.set(false);
                    String format = String.format("Waiter: name=[%s], hasWork=[%s], awaitNotCreate.", name, hasWork.get());
                    System.out.println(format);
                    queue.awaitNotCreate();
                }

                //测试线程意外终止的情况
                if (signalAllIterators > 2) {
                    String format = String.format("Waiter: name=[%s], hasWork=[%s], signalAllIterators=[%s], more than 2.", name, hasWork.get(), signalAllIterators);
                    System.out.println(format);
                    throw new RuntimeException(format);
                }

                hasWork.set(true);
                manager.sharedSignalAll();
                ++signalAllIterators;

                String format = String.format("Waiter: name=[%s], hasWork=[%s], sharedSignalAll.", name, hasWork.get());
                System.out.println(format);
                wasteTime(1, TimeUnit.SECONDS);
            } catch (Throwable throwable) {
                String message = throwable.getMessage();
                if (throwable instanceof InterruptedException) {
                    // restore the interrupt message from awaitNotCreate to make the Waiter finish
                    Thread.currentThread().interrupt();
                    String format = String.format("Waiter: name=[%s], hasWork=[%s], restore interrupt mark with InterruptedException=[%s].", name, hasWork.get(), message);
                    System.out.println(format);
                } else if (throwable instanceof RuntimeException) {
                    //注意线程不能被终止，
                    // 否则由于没有人会提交 Waiter 到 java.util.concurrent.Executor.execute 中了，导致没有对这个 queue 的监控 waiter 补充了。
                    //不过我们使用了一个虚假的队列 SharedWorkLockQueue 来提供 Waiter ，而该队列的 task poll 方法会直接返回 Waiter 对象，
                    // 所以即使在线程意外结束了，也不需要手动使用 Executor.execute 添加 Waiter 了。
                    throw (RuntimeException) throwable;
                } else {
                    String format = String.format("Waiter: name=[%s], hasWork=[%s], ignored Throwable=[%s].", name, hasWork.get(), message);
                    System.out.println(format);
                }
            }
        } while (!Thread.currentThread().isInterrupted());

        String format = String.format("Waiter: name=[%s], hasWork=[%s], finish.", name, hasWork.get());
        System.out.println(format);
    }

    private void wasteTime(long timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
