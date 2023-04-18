package concurrency.lock.condition.task;

import concurrency.lock.condition.role.SimplePool;

import java.util.concurrent.atomic.AtomicInteger;

public class Creator extends BaseTask {

    public static AtomicInteger executeCount = new AtomicInteger(0);
    public static AtomicInteger processCount = new AtomicInteger(0);
    public static AtomicInteger retryCount = new AtomicInteger(0);

    public Creator(SimplePool simplePool) {
        super(executeCount.incrementAndGet(), simplePool);
    }

    @Override
    public void run() {
        processCount.incrementAndGet();

        int waitCreateIterators = 0;

        do {
            //每次被唤醒时的所添加的对象，并不是批量重试的
            boolean retry = false;

            try {
                //有需求时才进行创建，防止由于 ThreadPoolExecutor.prestartAllCoreThreads 启动线程时，即使导致没有 invoker 时也进行添加
                while (simplePool.hasTakeWaiters()) {
                    simplePool.addEntity();

                    if (!retry) {
                        retry = true;
                    } else {
                        retryCount.incrementAndGet();
                    }
                }

                //测试线程意外终止的情况
                if (waitCreateIterators > 10_000) {
                    String simpleName = this.getClass().getSimpleName();
                    String name = Thread.currentThread().getName();
                    String format = String.format("interrupted:name=[%s], threadName=[%s], index=[%s], waitCreateIterators=[%s], more than 2", simpleName, name, getIndex(), waitCreateIterators);
                    throw new RuntimeException(format);
                }

                simplePool.awaitCreate();
                ++waitCreateIterators;
            } catch (Throwable throwable) {
                if (throwable instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                } else if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                } else {
                    throwable.printStackTrace();
                }
            }
        } while (!Thread.currentThread().isInterrupted());
    }

}
