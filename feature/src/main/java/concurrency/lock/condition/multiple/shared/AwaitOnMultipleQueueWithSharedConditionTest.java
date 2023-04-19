package concurrency.lock.condition.multiple.shared;

import concurrency.lock.condition.multiple.task.InvokeTask;
import concurrency.lock.condition.multiple.task.PoolTask;
import concurrency.lock.condition.queue.LinkedBlockingDeque;
import concurrency.lock.condition.util.ThreadPoolUtility;

import java.util.concurrent.TimeUnit;

public class AwaitOnMultipleQueueWithSharedConditionTest {

    private static final NoticableSharedCondition SHARED_CONDITION = new NoticableSharedCondition(false);
    private static final LinkedBlockingDeque<Runnable> one = new NoticableSharedLinkedBlockingDeque<>(false, SHARED_CONDITION);
    private static final LinkedBlockingDeque<Runnable> two = new NoticableSharedLinkedBlockingDeque<>(false, SHARED_CONDITION);

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolUtility.statisticTimeHook();

        // trigger poll signal
        Runnable poolTask = new PoolTask(one, two);
        Thread poolTaskThread = new Thread(poolTask, "poolTask");
        poolTaskThread.start();

        // trigger invoke task
        Runnable invokeTask = new InvokeTask();
        Thread invokeTaskThread = new RunTaskThread(invokeTask, "invokeTask");
        invokeTaskThread.start();

        // keep running
        ThreadPoolUtility.sleep(15, TimeUnit.SECONDS);

        System.out.println("shutdown");
        // interrupt running thread
        poolTaskThread.interrupt();
        invokeTaskThread.interrupt();

        //我们可以直接中断该线程，此时他也能从 await 中返回，所以对于 SHARED_CONDITION 可以不实现其 interruptTakeWaiters 方法
//        SHARED_CONDITION.interruptTakeWaiters();
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

            do {
                while (SHARED_CONDITION.hasTakeWaiters()) {
                    runnable.run();
                }

                try {
                    SHARED_CONDITION.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } while (!Thread.currentThread().isInterrupted());

            String finishFormat = String.format("Task: name=[%s], thread finish.", name);
            System.out.println(finishFormat);
        }
    }

}
