package ThreadPoolExecutorTest.destory;

import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 4/9/2019.
 */
public class SimulationTask implements Runnable {

    @Override
    public void run() {

        System.out.println("TaskThreadID: " + Thread.currentThread().getId());
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
