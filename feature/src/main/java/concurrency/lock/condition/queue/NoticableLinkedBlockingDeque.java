package concurrency.lock.condition.queue;

import java.time.Duration;
import java.util.concurrent.locks.Condition;

public class NoticableLinkedBlockingDeque<E> extends LinkedBlockingDeque<E> {

    private final Condition allowCreate;

    public NoticableLinkedBlockingDeque(boolean fairness) {
        this(Integer.MAX_VALUE, fairness);
    }

    public NoticableLinkedBlockingDeque(int capacity, boolean fairness) {
        super(capacity, fairness);
        allowCreate = lock.newCondition();
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
                allowCreate.signal();
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
                allowCreate.signal();
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
                allowCreate.signal();
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
                allowCreate.signal();
                notEmpty.await();
            }
            return x;
        } finally {
            lock.unlock();
        }
    }

    public void awaitAllowCreate() throws InterruptedException {
        // 我们这里期望有 poll 时能够唤醒 creator ，但是
        // poll 内部仅仅在没有数据时会 await not empty,并没有其他信号了，此时除非其他人 add 时触发 signal not empty 才能唤醒他们
        // 这里不太适合直接使用队列自身的的 not empty 和 not full 因为他们都有自己的用处。
        // 所以我们应该给 poll 独立的增加一个 signal not create 来触发对于 creator 线程的调度，然后没有要创建的东西时，他们 await not create 就行了。
        lock.lock();
        try {
            allowCreate.await();
        } finally {
            lock.unlock();
        }
    }

    public void signalAllowCreate() {
        lock.lock();
        try {
            allowCreate.signal();
        } finally {
            lock.unlock();
        }
    }

    public void signalAllAllowCreate() {
        // 注意 concurrency.lock.condition.queue.LinkedBlockingDeque 类的 poll 方法在 await not empty 前都需要 signal not create
        // 为了方便，我们直接写到对应的方法里面了，不过由于在 poll 这些方法中一般一个调用只是 poll 一个原始，所以里面都写的是 signal ，
        // 而这里由于是为了加快 creator 线程的运行，所以其直接使用了 signal all 来通知所有潜在 await 的线程。
        lock.lock();
        try {
            allowCreate.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean trySignalAllAllowCreate() {
        if (lock.tryLock()) {
            try {
                allowCreate.signalAll();
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

}