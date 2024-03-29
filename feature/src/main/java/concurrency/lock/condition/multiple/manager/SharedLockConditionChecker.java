package concurrency.lock.condition.multiple.manager;

import concurrency.lock.condition.queue.NoticableLinkedBlockingDeque;

import java.util.concurrent.atomic.AtomicInteger;

public class SharedLockConditionChecker<E> implements Runnable {

    private static final AtomicInteger counter = new AtomicInteger(0);
    private final int index = counter.incrementAndGet();

    private final SharedLockConditionManager<E> manager;
    private final NoticableLinkedBlockingDeque<E> queue;

    public SharedLockConditionChecker(SharedLockConditionManager<E> manager, NoticableLinkedBlockingDeque<E> queue) {
        this.manager = manager;
        this.queue = queue;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();

        int signalAllIterators = 0;

        do {
            try {
                while (!hasWork()) {
                    String format = String.format("Checker: index=[%s], threadName=[%s], awaitAllowCreate.", index, name);
                    System.out.println(format);
                    queue.awaitAllowCreate();
                }

                //测试线程意外终止的情况
                if (signalAllIterators > 2) {
                    String format = String.format("Checker: index=[%s], threadName=[%s], signalAllIterators=[%s], more than 2.", index, name, signalAllIterators);
                    System.out.println(format);
                    throw new RuntimeException(format);
                } else {
                    ++signalAllIterators;
                }

                while (hasWork()) {
                    String format = String.format("Checker: index=[%s], threadName=[%s], signalAllowRunAndAwaitCheckWork.", index, name);
                    System.out.println(format);
                    manager.signalAllowRunAndAwaitCheckWork();
                }
            } catch (Throwable throwable) {
                String message = throwable.getMessage();
                if (throwable instanceof InterruptedException) {
                    // restore the interrupt message from awaitAllowCreate to make the Waiter finish
                    Thread.currentThread().interrupt();
                    String format = String.format("Checker: index=[%s], threadName=[%s], restore interrupt mark with InterruptedException=[%s].", index, name, message);
                    System.out.println(format);
                } else if (throwable instanceof RuntimeException) {
                    //注意线程不能被终止，
                    // 否则由于没有人会提交 Waiter 到 java.util.concurrent.Executor.execute 中了，导致没有对这个 queue 的监控 waiter 补充了。
                    //不过我们使用了一个虚假的队列 SharedWorkLockQueue 来提供 Waiter ，而该队列的 task poll 方法会直接返回 Waiter 对象，
                    // 所以即使在线程意外结束了，也不需要手动使用 Executor.execute 添加 Waiter 了。
                    throw (RuntimeException) throwable;
                } else {
                    String format = String.format("Checker: index=[%s], threadName=[%s], ignored Throwable=[%s].", index, name, message);
                    System.out.println(format);
                }
            }
        } while (!Thread.currentThread().isInterrupted());

        String format = String.format("Checker: index=[%s], threadName=[%s], finish.", index, name);
        System.out.println(format);
    }

    public boolean hasWork() {
        return queue.hasTakeWaiters();
    }

}
