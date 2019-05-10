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

        DestroyableThreadPoolExecutor executor = new DestroyableThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, blockingQueue, destroyableThreadFactory);
//        executor.allowCoreThreadTimeOut(true);

        int index = 3;
        for (int i = 0; i < index; ++i) {
            SimulationTask simulationTask = new SimulationTask();
            executor.submit(simulationTask);
        }

        try {
            executor.shutdown();
            executor.awaitTermination(1000, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
