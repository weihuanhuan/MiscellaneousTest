package concurrency.lock.readwrite;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockChecker {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private final Lock lock = new ReentrantLock();

    private final AtomicLong counter = new AtomicLong(0L);

    private final AtomicLong lastStart = new AtomicLong(0L);

    private volatile ReadWriteLockResult result;

    public boolean check() {
        ReadWriteLockResult localResult = result;
        if (localResult == null) {
            localResult = start();
        }

        boolean result = result(localResult);

        if (this.result != null) {
            reset();
        }
        return result;
    }

    private ReadWriteLockResult start() {
        lock.lock();
        try {
            //由于没有 synchronized result ，所以需要在 writeLock 内部在再次确认他是否为 null
            if (result != null) {
                return result;
            }

            //本地副本，避免在该实例对象还未完成初始化时，将其提前发布到方法作用域外面，以外放使用错误的对象
            ReadWriteLockResult localTask;
            localTask = new ReadWriteLockResult(() -> check((counter)));

            Thread thread = new Thread(localTask);
            String threadName = String.format("thread-[%s]-[%s]", localTask.getClass()
                    .getSimpleName(), counter.incrementAndGet());
            thread.setName(threadName);
            thread.setDaemon(true);
            thread.start();

            String name = Thread.currentThread().getName();
            long start = System.currentTimeMillis();
            long offset = start - lastStart.get();
            lastStart.set(start);

            String format = String.format("name=[%s], start, thread=[%s], start=[%s], offset=[%s]", name, threadName, start, offset);
            System.out.println(format);

            //本地副本，我们在该实例对象完全构造好之后，再将其引用发布到方法作用域外面
            result = localTask;
            return result;
        } finally {
            lock.unlock();
        }
    }

    private boolean result(ReadWriteLockResult localResult) {
        //无需 readLock , 因为我们仅仅在 start 和 reset 中操作 result 对象，而他又仅仅被 writeLock 所控制，
        // 而在读取 localResult 时，我们也是直接使用的 start 中传递过来的 local copy 实例，所以这里其实可以安全的访问，无需加锁控制。
        // 这里我们可以替换为更简单的普通 java.util.concurrent.locks.ReentrantLock 就可以了。
//        readLock.lock();
        try {
            //本地副本，避免在调用该对象的多个方法时，该引用所指的对象实例发生变化，比如其变为了 null
            //但是这个方法依旧会获取到 result 为 null 的情况，这是由于本线程可能恰巧会在其他线程 reset 后，来调用这个方法
            //主要是我们的先前的 reset 的 done 完成，可能发生在其他线程调用时，本线程正常处于执行 start 和 result 之间导致 result 为 null 了
            //而我们这里使用 readLock 和 reset 的 writeLock 就是碰巧在这个缝隙就没了互斥的关系。
            //解决方案可以使用 start 方法将保证非 null 的 result 对象传递过来，而不是再次获取，
            //ReadWriteLockTask localResult = result;
            if (localResult == null) {
                String name = Thread.currentThread().getName();
                long start = System.currentTimeMillis();
                String format = String.format("name=[%s], result, thread=[%s], localResult=[null]", name, start);
                System.out.println(format);
                throw new IllegalStateException(format);
            }

            localResult.waitResult();
            return localResult.queryResult();
        } finally {
//            readLock.unlock();
        }
    }

    private void reset() {
        if (lock.tryLock()) {
            try {
                if (result == null) {
                    return;
                }

                //需要增加这个判断，否则 offset 可能存在小于 callTime 的情况
                //这是因为我们这里的 writeLock 和 readLock 只是控制了是否有 user 线程在访问 result ，
                //而没有关注一个 result 是否真正的完成了，这会导致一个未完成的 result 依据存在可获取 writeLock 的情况，
                //进而在一个 result 还在运行时就创建了，另一个新的 task ，
                //也就是说就的 result 线程还未停止，就创建了新的 result 线程，所以导致
                // 1.出现 offset 小于 callTime 的情况
                // 2.对于同一个 ReadWriteLockChecker ，启动了多个 thread ，
                //这是不必要的，我们希望只要存在 thread 在进行 check ，其他线程应该直接使用当前正在检查的结果。
                if (!result.isDone()) {
                    return;
                }
                result = null;
            } finally {
                lock.unlock();
            }
        }
    }

    private Boolean check(AtomicLong counter) throws SQLException {
        long counterLong = counter.get();
        ReadWriteLockCallable readWriteLockCallable = new ReadWriteLockCallable(counterLong);
        return readWriteLockCallable.call();
    }

}
