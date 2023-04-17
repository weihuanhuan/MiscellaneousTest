package concurrency.lock.condition.role;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import concurrency.lock.condition.entity.Entity;
import concurrency.lock.condition.handler.CreatorSignalExecutionHandler;
import concurrency.lock.condition.handler.CreatorThreadFactory;
import concurrency.lock.condition.handler.LoggingExecutionHandler;
import concurrency.lock.condition.util.ThreadPoolUtility;

// jdk 的 LinkedBlockingDeque 不支持配置锁是否公平，无法获取到是否有线程在该队列上等待
//import java.util.concurrent.LinkedBlockingDeque;
// dbcp2 的 LinkedBlockingDeque is not public in org.apache.commons.pool2.impl; cannot be accessed from outside package
//import org.apache.commons.pool2.impl.LinkedBlockingDeque;
// 所以我们从 dbcp2 的源码直接 copy 了一份，并修改其包可见性为 public
import concurrency.lock.condition.queue.LinkedBlockingDeque;
import concurrency.lock.condition.queue.NoticableLinkedBlockingDeque;

public class SimplePool {

    // pool statistic
    public static AtomicInteger transferAddCount = new AtomicInteger(0);
    public static AtomicInteger transferBorrowCount = new AtomicInteger(0);

    // pool config
    private final boolean useFairnessLock;
    private final boolean useTransferQueue;
    private final int creatorThread;

    // entity queue
    private LinkedBlockingDeque<Entity> createdQueue;
    private LinkedTransferQueue<Entity> transferQueue;

    // creator executor
    private ThreadPoolExecutor creatorThreadPoolExecutor;

    // creator lock
    private final Lock createLock = new ReentrantLock();
    private final Condition needCreate = createLock.newCondition();

    public SimplePool(boolean useFairnessLock, boolean useTransferQueue, int creatorThread) {
        this.useFairnessLock = useFairnessLock;
        this.useTransferQueue = useTransferQueue;
        this.creatorThread = creatorThread;

        initPool();
    }

    private void initPool() {
        createdQueue = new NoticableLinkedBlockingDeque<>(useFairnessLock);

        if (useTransferQueue) {
            transferQueue = new LinkedTransferQueue<>();
        }

        ThreadFactory threadFactory = new CreatorThreadFactory("creator", true, this);
        RejectedExecutionHandler handler = new CreatorSignalExecutionHandler(this);
        handler = new LoggingExecutionHandler(this);

        creatorThreadPoolExecutor = ThreadPoolUtility.createThreadPoolExecutor(1, creatorThread, creatorThread, null, threadFactory, handler);
        creatorThreadPoolExecutor.prestartAllCoreThreads();
    }

    // **************** call by invoker ****************
    public Entity borrowEntity() throws InterruptedException {
        Entity entity = createdQueue.pollFirst();

        if (entity == null) {
            // 这里存在漏洞，
            // 比如在 needCreate signal 后，如果 transferQueue 或者 createdQueue 的 poll 还没来得急调用，
            // 而 creator 线程那边则里面 wakeup 了并在 poll 之前就快速检测了 has wait ，那么他就看见依旧没有 wait 的线程，随后他就错误的继续 needCreate await 了
            // 所以这里的 wakeup 条件应该使用 transferQueue 或者 createdQueue 的 signal 才对
            wakeupCreator();

            if (useTransferQueue) {
                entity = transferQueue.poll(100, TimeUnit.MILLISECONDS);
                if (entity != null) {
                    transferBorrowCount.incrementAndGet();
                }
            }
        }

        if (entity == null) {
            entity = createdQueue.pollFirst(1000, TimeUnit.MILLISECONDS);
            //不需要手动调用 notFull signal, 因为 pollFirst 内部会调用
//            notFull.signalAll();
        }
        return entity;
    }

    private void wakeupCreator() {
        // 这里一定要通知到，
        // 比如只有一个线程创建时，如果他 lock 了锁，并准备调用 await ，
        // 而此时假如当前线程正准备执行 signal ，则他的 try lock 就会失败，然后这个通知就丢了，
        // 如果接下来没有别的触发 signal 的动作了，那么就导致创建线程无法 wakeup 了
        signalCreate();
    }

    private void signalCreate() {
        //由于 signal 不会阻塞，所以不存在 await 时的【在 needCreate 还是 notCreate 上，哪个比较好，能不能同时考虑这两个情况】 的问题
        transferSignalNeedCreate();

        //一般 queue 的 poll 方法就会触发该信号了，我们这里可以在 poll 之前额外的发送该信号，加快处理
        queueSignalNotCreate();
    }

    private void transferSignalNeedCreate() {
        if (useTransferQueue) {
            createLock.lock();
            try {
                needCreate.signalAll();
            } finally {
                createLock.unlock();
            }
        }
    }

    private void queueSignalNotCreate() {
        if (createdQueue instanceof NoticableLinkedBlockingDeque) {
            NoticableLinkedBlockingDeque.class.cast(createdQueue).signalAllNotCreate();
        }
    }

    public boolean trySignalCreate() {
        boolean signaledTransfer = tryTransferSignalCreate();
        boolean signaledQueue = tryQueueSignalNotCreate();
        return signaledTransfer || signaledQueue;
    }

    private boolean tryTransferSignalCreate() {
        if (createLock.tryLock()) {
            try {
                needCreate.signalAll();
                return true;
            } finally {
                createLock.unlock();
            }
        } else {
            return false;
        }
    }

    private boolean tryQueueSignalNotCreate() {
        if (createdQueue instanceof NoticableLinkedBlockingDeque) {
            return NoticableLinkedBlockingDeque.class.cast(createdQueue).trySignalAllNotCreate();
        }
        return false;
    }

    // **************** call by creator ****************
    public void addEntity() throws InterruptedException {
        Entity entity = createEntity();

        boolean added = false;
        if (useTransferQueue) {
            added = transferQueue.tryTransfer(entity, 1000, TimeUnit.MILLISECONDS);
            if (added) {
                transferAddCount.incrementAndGet();
            }
        }

        if (!added) {
            createdQueue.addLast(entity);
            //不需要手动调用 notEmpty signal, 因为 addLast 内部会调用
//            notEmpty.signalAll();
        }
    }

    private Entity createEntity() {
        Entity entity = new Entity();
        return entity;
    }

    public boolean hasTakeWaiters() {
        // 检测是否有等待的线程时只有 transfer 或 queue 任意一个满足就行了
        // 由于 creator 也需要依据 transfer 的等候数量来创建 ，所以这里的必要检测 transfer
        boolean has = transferHasTakeWaiters();

        if (!has) {
            has = queueHasTakeWaiters();
        }
        return has;
    }

    private boolean transferHasTakeWaiters() {
        if (useTransferQueue) {
            return transferQueue.hasWaitingConsumer();
        }
        return false;
    }

    private boolean queueHasTakeWaiters() {
        return createdQueue.hasTakeWaiters();
    }

    public void awaitCreate() throws InterruptedException {
        //JF TODO 这里有个问题，当开启 useTransferQueue 后，由于 await 操作会阻塞，
        //JF TODO 所以到底是 await 在 needCreate 还是 notCreate 上，哪个比较好，能不能同时考虑这两个情况
        transferAwaitNeedCreate();

        queueAwaitNotCreate();
    }

    private void transferAwaitNeedCreate() throws InterruptedException {
        //使用 isUseTransferQueue 时才需要 await create
        if (useTransferQueue) {
            createLock.lock();
            try {
                needCreate.await();
            } finally {
                createLock.unlock();
            }
        }
    }

    private void queueAwaitNotCreate() throws InterruptedException {
        if (createdQueue instanceof NoticableLinkedBlockingDeque) {
            NoticableLinkedBlockingDeque.class.cast(createdQueue).awaitNotCreate();
        }
    }

    // **************** call by pool ****************
    public void shutdown(long timeout, TimeUnit timeUnit) throws InterruptedException {
        creatorThreadPoolExecutor.shutdown();
        creatorThreadPoolExecutor.awaitTermination(timeout, timeUnit);
    }

}
