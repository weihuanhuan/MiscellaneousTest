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

        //由于没有使用 readLock 来控制查询状态，所以即使有其他线程在执行 result 时，checker 的 result 也可能被 reset 调。
        // 因此这里就只能让同时拿到了 local copy result 实例对象的线程共享相同的结果。
        //     而任何一个线程一旦 reset 了 result ，那么此后的线程就只能看见新的 result 的状态了，
        //     也就是此时 result 的有效状态时间为任何第一个看见 result 的结果的时候
        // 这里虽然我们可使用 readLock 做到完美控制只要存在任何线程查询 result 时，都不能让执行 reset 成功。
        //     但是这会导致在前一个线程执行查询而占用了 readLock 时，而正好本次线程触发了新的 result 对象构建，需要 writeLock
        //     那么本线程就要等到前一个线程的 readLock 结束才能获取到新的 writeLock， 这使得本线程的等到时间会突破预定的 waitTimeout，比如下面的场景
        //         thread-1，处于执行 reset 前，他准备清理旧的 result
        //         thread-2，处于执行 result 前，他持有旧的 local copy result ，然后使用 readLock 并等到 waitTimeout
        //         thread-3，处于进入 check 后，他需要创建新的 result，此时他需要 writeLock ，故只能等到 thread-2 释放 readLock，
        //                  并在创建之后再次使用 readLock 并等到 waitTimeout，所以他就变成了等待两次 readLock 的时间。
        //     为了更加精准的保证 waitTimeout ，所以我们舍弃了使用 readLock 对查询 result 结果的控制，这种处理会导致并发能力将低了不少
        //     毕竟如果借助 readLock ，我们可以将 result 的有效状态时间延长到在没有任何线程查看 result 结果的时候，那么就有更多的线程可以共享结果了。
        boolean ok = result(localResult);

        if (this.result != null) {
            reset();
        }
        return ok;
    }

    private ReadWriteLockResult start() {
        lock.lock();
        try {
            //由于没有 synchronized result ，所以需要在 writeLock 内部在再次确认他是否为 null
            if (result != null) {
                return result;
            }

            //本地副本，避免在该实例对象还未完成初始化时，将其提前发布到方法作用域外面，以外放使用错误的对象
            ReadWriteLockResult localResult;
            localResult = new ReadWriteLockResult(() -> check((counter)));

            Thread thread = new Thread(localResult);
            String threadName = String.format("thread-[%s]-[%s]", localResult.getClass()
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
            result = localResult;
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
