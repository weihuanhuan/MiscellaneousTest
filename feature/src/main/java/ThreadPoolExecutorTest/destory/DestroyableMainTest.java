package ThreadPoolExecutorTest.destory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 4/9/2019.
 */
public class DestroyableMainTest {

    public static void main(String[] args) {

        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(10);

        DestroyableThreadFactory destroyableThreadFactory = new DestroyableThreadFactory();

        //减少超时时间加速线程退出，运同时运行 core 线程超时，使得池中的线程数量可以降为 0 。
        DestroyableThreadPoolExecutor executor = new DestroyableThreadPoolExecutor(0, 10, 1, TimeUnit.SECONDS, blockingQueue, destroyableThreadFactory);
        executor.allowCoreThreadTimeOut(true);

        int index = 3;
        for (int i = 0; i < index; ++i) {
            SimulationTask simulationTask = new SimulationTask();
            executor.submit(simulationTask);
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //即使每次线程都被异常所终止，最后池中还是会最少存在一个核心线程，用来处理队列里面的任务，
        //除非设置了 allowCoreThreadTimeOut 且这最后一个线程发生了超时才会出现 getPoolSize 为 0 的情况 。
        int poolSize = executor.getPoolSize();
        System.out.println();
        System.out.println("executor.getPoolSize():" + poolSize);

        //关闭线程池，会结束所有的线程，此时才可以推出，否则要等线程的自然的超时才行
        try {
            executor.shutdown();
            executor.awaitTermination(1000, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
