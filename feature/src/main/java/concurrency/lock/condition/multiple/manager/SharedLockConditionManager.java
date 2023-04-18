package concurrency.lock.condition.multiple.manager;

import concurrency.lock.condition.queue.InterruptibleReentrantLock;
import concurrency.lock.condition.queue.NoticableLinkedBlockingDeque;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;

import static java.util.concurrent.TimeUnit.SECONDS;

public class SharedLockConditionManager<E> {

    private final List<NoticableLinkedBlockingDeque<E>> deques = new ArrayList<>();

    private final List<AtomicBoolean> atomicBooleans = new ArrayList<>();
    private final List<ThreadPoolExecutor> threadPoolExecutors = new ArrayList<>();

    private final InterruptibleReentrantLock sharedLock = new InterruptibleReentrantLock(false);
    private final Condition noWork = sharedLock.newCondition();

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

        deque.remove(deque);
    }

    public void start() throws InterruptedException {
        for (NoticableLinkedBlockingDeque<E> deque : deques) {
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            SharedLockConditionWaiter<E> waiter = new SharedLockConditionWaiter<>(this, deque, atomicBoolean);

            SharedLockConditionQueue queue = new SharedLockConditionQueue(waiter);
            SharedLockConditionThreadFactory threadFactory = new SharedLockConditionThreadFactory();
            RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();

            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 5L, SECONDS, queue, threadFactory, handler);
            threadPoolExecutor.prestartAllCoreThreads();
            threadPoolExecutor.allowCoreThreadTimeOut(false);

            atomicBooleans.add(atomicBoolean);
            threadPoolExecutors.add(threadPoolExecutor);
        }
    }

    public void shutdown() {
        for (ThreadPoolExecutor threadPoolExecutor : threadPoolExecutors) {
            threadPoolExecutor.shutdownNow();
        }

        sharedInterruptWaiters();
    }

    private void sharedInterruptWaiters() {
        sharedLock.lock();
        try {
            sharedLock.interruptWaiters(noWork);
        } finally {
            sharedLock.unlock();
        }
    }

    public void sharedSignalAll() {
        sharedLock.lock();
        try {
            noWork.signalAll();
        } finally {
            sharedLock.unlock();
        }
    }

    public void sharedRun(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        String name = Thread.currentThread().getName();

        do {
            while (anyDequeHasWork()) {
                runnable.run();
            }

            try {
                String format = String.format("sharedRun: name=[%s], sharedAwait.", name);
                System.out.println(format);
                sharedAwait();
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
        for (AtomicBoolean value : atomicBooleans) {
            boolean hasWork = value != null && value.get();
            if (hasWork) {
                return true;
            }
        }
        return false;
    }

    private void sharedAwait() throws InterruptedException {
        sharedLock.lock();
        try {
            noWork.await();
        } catch (InterruptedException e) {
            // MUST be throw to propagate InterruptedException make the sharedRun finish
            throw e;
        } finally {
            sharedLock.unlock();
        }
    }

}
