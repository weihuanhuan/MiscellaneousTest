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

    private final SharedLockConditionRunner<E> runner = new SharedLockConditionRunner<>(this);
    private final List<SharedLockConditionChecker<E>> checkers = new ArrayList<>();
    private final List<ThreadPoolExecutor> threadPoolExecutors = new ArrayList<>();

    private final InterruptibleReentrantLock sharedLock = new InterruptibleReentrantLock(false);
    private final Condition allowRun = sharedLock.newCondition();
    private final Condition checkWork = sharedLock.newCondition();

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
            SharedLockConditionChecker<E> checker = new SharedLockConditionChecker<>(this, deque);
            SharedLockConditionThreadFactory threadFactory = new SharedLockConditionThreadFactory("checker", true);
            ThreadPoolExecutor endlessThreadPoolExecutor = ThreadPoolUtility.createEndlessThreadPoolExecutor(1, checker, threadFactory);

            checkers.add(checker);
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
            sharedLock.interruptWaiters(allowRun);
            sharedLock.interruptWaiters(checkWork);
        } finally {
            sharedLock.unlock();
        }
    }

    /*************************** call by user ***************************/

    public void sharedRun(Runnable runnable) {
        runner.sharedRun(runnable);
    }

    /*************************** call by checker ***************************/

    public void signalAllowRunAndAwaitCheckWork() throws InterruptedException {
        sharedLock.lock();
        try {
            allowRun.signalAll();
            checkWork.await();
        } finally {
            sharedLock.unlock();
        }
    }

    /*************************** call by runner ***************************/

    public boolean anyDequeHasWork() {
        for (SharedLockConditionChecker<E> waiter : checkers) {
            if (waiter.hasWork()) {
                return true;
            }
        }
        return false;
    }

    public void signalCheckWorkAndAwaitAllowRun() throws InterruptedException {
        sharedLock.lock();
        try {
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
