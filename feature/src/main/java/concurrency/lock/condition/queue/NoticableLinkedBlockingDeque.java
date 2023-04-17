package concurrency.lock.condition.queue;

import java.time.Duration;
import java.util.concurrent.locks.Condition;

public class NoticableLinkedBlockingDeque<E> extends LinkedBlockingDeque<E> {

    private final Condition notCreate;

    public NoticableLinkedBlockingDeque(boolean fairness) {
        super(fairness);
        notCreate = lock.newCondition();
    }

    public NoticableLinkedBlockingDeque(int capacity, boolean fairness) {
        super(capacity, fairness);
        notCreate = lock.newCondition();
    }

    @Override
    public E pollFirst(final Duration timeout) throws InterruptedException {
        long nanos = timeout.toNanos();
        lock.lockInterruptibly();
        try {
            E x;
            while ((x = unlinkFirst()) == null) {
                if (nanos <= 0) {
                    return null;
                }
                notCreate.signal();
                nanos = notEmpty.awaitNanos(nanos);
            }
            return x;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E pollLast(final Duration timeout) throws InterruptedException {
        long nanos = timeout.toNanos();
        lock.lockInterruptibly();
        try {
            E x;
            while ((x = unlinkLast()) == null) {
                if (nanos <= 0) {
                    return null;
                }
                notCreate.signal();
                nanos = notEmpty.awaitNanos(nanos);
            }
            return x;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E takeFirst() throws InterruptedException {
        lock.lock();
        try {
            E x;
            while ((x = unlinkFirst()) == null) {
                notCreate.signal();
                notEmpty.await();
            }
            return x;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public E takeLast() throws InterruptedException {
        lock.lock();
        try {
            E x;
            while ((x = unlinkLast()) == null) {
                notCreate.signal();
                notEmpty.await();
            }
            return x;
        } finally {
            lock.unlock();
        }
    }

    public void awaitNotCreate() throws InterruptedException {
        lock.lock();
        try {
            notCreate.await();
        } finally {
            lock.unlock();
        }
    }

    public void signalNotCreate() {
        lock.lock();
        try {
            notCreate.signal();
        } finally {
            lock.unlock();
        }
    }

    public void signalAllNotCreate() {
        lock.lock();
        try {
            notCreate.signalAll();
        } finally {
            lock.unlock();
        }
    }

}