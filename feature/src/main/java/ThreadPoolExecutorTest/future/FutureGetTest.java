package ThreadPoolExecutorTest.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 9/7/2020.
 */
public class FutureGetTest {

    public static void main(String[] args) throws Exception {

        System.out.println("############### testThreadJoin ################");
        testThreadJoin();

        System.out.println("############### testFutureGet ################");
        testFutureGet();

        //可以抛出 null 异常，表现为这里直接产生 NEP
        throw null;
    }

    private static void testThreadJoin() throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Thread thread = Thread.currentThread();
                try {
                    System.out.println(thread + " " + System.currentTimeMillis());
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(thread + " " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        thread.join();
    }

    public static void testFutureGet() throws Exception {
        Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread thread = Thread.currentThread();
                System.out.println(thread + " " + System.currentTimeMillis());
                TimeUnit.SECONDS.sleep(1);
                System.out.println(thread + " " + System.currentTimeMillis());
                return null;
            }
        };
        //直接执行 callable
        callable.call();

        //在线程池中被调度的 FutureTask 将会阻塞，直到任务完成。
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        FutureTask<Void> futureTaskWithExecutor = new FutureTask<>(callable);
        executorService.submit(futureTaskWithExecutor);
        futureTaskWithExecutor.get();
        executorService.shutdown();

        //而如果没有线程池对其进行调度，所以他将一直等待某个可用的现场线程来执行，
        //而在其 futureTask.get() 内部的状态由于没有线程来执行本身，所以其状态无法更新为 done ，故其将永远的等待下去。
        FutureTask<Void> futureTask = new FutureTask<>(callable);
        futureTask.get();

//        "main" #1 prio=5 os_prio=0 tid=0x0000000002a14800 nid=0xb60 waiting on condition [0x00000000028bf000]
//        java.lang.Thread.State: WAITING (parking)
//        at sun.misc.Unsafe.park(Native Method)
//                - parking to wait for  <0x0000000780e8fd68> (a java.util.concurrent.FutureTask)
//        at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
//        at java.util.concurrent.FutureTask.awaitDone(FutureTask.java:429)
//        at java.util.concurrent.FutureTask.get(FutureTask.java:191)
//        at ThreadPoolExecutorTest.future.FutureGetTest.testFutureGet(FutureGetTest.java:72)
//        at ThreadPoolExecutorTest.future.FutureGetTest.main(FutureGetTest.java:20)
    }

}
