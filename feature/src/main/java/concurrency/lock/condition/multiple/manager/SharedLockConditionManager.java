package concurrency.lock.condition.multiple.manager;

import concurrency.lock.condition.queue.InterruptibleReentrantLock;
import concurrency.lock.condition.queue.NoticableLinkedBlockingDeque;
import concurrency.lock.condition.util.ThreadPoolUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class SharedLockConditionManager<E> {

    private final List<NoticableLinkedBlockingDeque<E>> deques = new ArrayList<>();

    private final List<SharedLockConditionWaiter<E>> waiters = new ArrayList<>();
    private final List<ThreadPoolExecutor> threadPoolExecutors = new ArrayList<>();

    private final InterruptibleReentrantLock sharedLock = new InterruptibleReentrantLock(false);
    private final Condition allowRun = sharedLock.newCondition();
    private final Condition checkWork = sharedLock.newCondition();

    private boolean isShutdown = false;

    public void addDeque(NoticableLinkedBlockingDeque<E> deque) {
        if (deque == null) {
            return;
        }

        deques.add(deque);
    }

    public void removeDeque(NoticableLinkedBlockingDeque<E> deque) {
        if (deque == null) {
            return;
        }

        deques.remove(deque);
    }

    public void start() {
        for (NoticableLinkedBlockingDeque<E> deque : deques) {
            SharedLockConditionWaiter<E> waiter = new SharedLockConditionWaiter<>(this, deque);
            SharedLockConditionThreadFactory threadFactory = new SharedLockConditionThreadFactory("waiter", true);
            ThreadPoolExecutor endlessThreadPoolExecutor = ThreadPoolUtility.createEndlessThreadPoolExecutor(1, waiter, threadFactory);

            waiters.add(waiter);
            threadPoolExecutors.add(endlessThreadPoolExecutor);
        }
    }

    public void shutdown() {
        for (ThreadPoolExecutor threadPoolExecutor : threadPoolExecutors) {
            threadPoolExecutor.shutdownNow();
            try {
                threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }

        sharedInterruptWaiters();
    }

    private void sharedInterruptWaiters() {
        sharedLock.lock();
        try {
            isShutdown = true;

            //注意之类调用 interrupt 时，可能 sharedRun 正在运行，还没 await ，就会导致这个信号丢失了。
            //在其没有 await 时，我们这里使用 isShutdown 来标记 sharedRun 还是否应该 await 了。
            sharedLock.interruptWaiters(allowRun);
            sharedLock.interruptWaiters(checkWork);
        } finally {
            sharedLock.unlock();
        }
    }

    public void sharedSignalAll() throws InterruptedException {
        sharedLock.lock();
        try {
            allowRun.signalAll();
            checkWork.await();
        } finally {
            sharedLock.unlock();
        }
    }

    public void sharedRun(Runnable runnable) {
        String name = Thread.currentThread().getName();

        do {
            try {
                while (!anyDequeHasWork()) {
                    String format = String.format("sharedRun: name=[%s], sharedAwait.", name);
                    System.out.println(format);
                    sharedAwait();
                }

                while (anyDequeHasWork()) {
                    runnable.run();
                }
            } catch (InterruptedException e) {
                // restore the interrupt message from sharedAwait to make the sharedRun finish
                Thread.currentThread().interrupt();
                String format = String.format("sharedRun: name=[%s], restore interrupt mark with InterruptedException=[%s].", name, e.getMessage());
                System.out.println(format);
            }
        } while (!Thread.currentThread().isInterrupted());

        String format = String.format("sharedRun: name=[%s], finish.", name);
        System.out.println(format);
    }

    private boolean anyDequeHasWork() {
        for (SharedLockConditionWaiter<E> waiter : waiters) {
            if (waiter.hasWork()) {
                return true;
            }
        }
        return false;
    }

    private void sharedAwait() throws InterruptedException {
        sharedLock.lock();
        try {
            if (isShutdown) {
                throw new InterruptedException("noWork: sharedAwait, manager is shutdown");
            }

            checkWork.signalAll();
            allowRun.await();
        } catch (InterruptedException e) {
            // MUST be throw to propagate InterruptedException make the sharedRun finish
            throw e;
        } finally {
            sharedLock.unlock();
        }
    }

}
