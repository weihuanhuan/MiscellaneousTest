package concurrency.lock.condition.multiple;

import concurrency.lock.condition.multiple.manager.SharedLockConditionManager;
import concurrency.lock.condition.queue.NoticableLinkedBlockingDeque;
import concurrency.lock.condition.task.BaseTask;
import concurrency.lock.condition.util.ThreadPoolUtility;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AwaitOnMultipleQueueTest {

    private static final SharedLockConditionManager<BaseTask> manager = new SharedLockConditionManager<>();

    private static final NoticableLinkedBlockingDeque<BaseTask> one = new NoticableLinkedBlockingDeque<>(false);
    private static final NoticableLinkedBlockingDeque<BaseTask> two = new NoticableLinkedBlockingDeque<>(false);

    public static void main(String[] args) throws InterruptedException {
        manager.addDeque(one);
        manager.addDeque(two);
        manager.start();

        ThreadPoolUtility.sleep(2, TimeUnit.SECONDS);

        statisticTimeHook();

        // trigger pool signal
        Runnable poolTask = new PoolTask();
        // invoke task NOT using manager
        Thread poolTaskThread = new Thread(poolTask, "poolTask");
        poolTaskThread.start();

        Runnable invokeTask = new InvokeTask();
        // invoke task using manager
        Thread invokeTaskThread = new RunTaskThread(invokeTask, "invokeTask");
        invokeTaskThread.start();

        ThreadPoolUtility.sleep(15, TimeUnit.SECONDS);

        System.out.println("shutdown");
        manager.shutdown();
    }


    private static void statisticTimeHook() {
        long startTime = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Total Running Time:" + TimeUnit.MILLISECONDS.toSeconds(duration));
        }));
    }

    private static class PoolTask implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("one poll");
                BaseTask onePoll = one.poll(1, TimeUnit.SECONDS);
                System.out.println("two poll");
                //时间长一点，来验证 monitor 线程多次异常结束的情况，看能否自动补充结束的线程
                BaseTask twoPoll = two.poll(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class InvokeTask implements Runnable {

        private final AtomicInteger atomicInteger = new AtomicInteger(0);

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            int sum = atomicInteger.incrementAndGet();
            String format = String.format("Task: name=[%s], run once, sum=[%s].", name, sum);
            System.out.println(format);

            ThreadPoolUtility.sleep(1, TimeUnit.SECONDS);
        }
    }

    private static class RunTaskThread extends Thread {

        private final Runnable runnable;

        public RunTaskThread(Runnable runnable, String name) {
            super(name);
            this.runnable = runnable;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            String startFormat = String.format("Task: name=[%s], thread start.", name);
            System.out.println(startFormat);

            manager.sharedRun(runnable);

            String finishFormat = String.format("Task: name=[%s], thread finish.", name);
            System.out.println(finishFormat);
        }
    }

}
