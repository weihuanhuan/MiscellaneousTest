package concurrency.lock.condition.multiple.manager;

import concurrency.lock.condition.multiple.task.InvokeTask;
import concurrency.lock.condition.multiple.task.PoolTask;
import concurrency.lock.condition.queue.NoticableLinkedBlockingDeque;
import concurrency.lock.condition.util.ThreadPoolUtility;

import java.util.concurrent.TimeUnit;

public class AwaitOnMultipleQueueTest {

    private static final SharedLockConditionManager<Runnable> manager = new SharedLockConditionManager<>();

    private static final NoticableLinkedBlockingDeque<Runnable> one = new NoticableLinkedBlockingDeque<>(false);
    private static final NoticableLinkedBlockingDeque<Runnable> two = new NoticableLinkedBlockingDeque<>(false);

    public static void main(String[] args) throws InterruptedException {
        manager.addDeque(one);
        manager.addDeque(two);
        manager.start();

        ThreadPoolUtility.sleep(2, TimeUnit.SECONDS);

        ThreadPoolUtility.statisticTimeHook();

        // trigger poll signal
        Runnable poolTask = new PoolTask(one, two);
        // invoke task NOT using manager
        Thread poolTaskThread = new Thread(poolTask, "poolTask");
        poolTaskThread.start();

        // trigger invoke task
        Runnable invokeTask = new InvokeTask();
        // invoke task using manager
        Thread invokeTaskThread = new RunTaskThread(invokeTask, "invokeTask");
        invokeTaskThread.start();

        ThreadPoolUtility.sleep(15, TimeUnit.SECONDS);

        System.out.println("shutdown");
        manager.shutdown();
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
