package SystemTest.process.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ParentProcessExit implements Runnable {

    private final ExecutorService executorService;

    private final int seconds;

    private final boolean shutdownExecutor;

    public ParentProcessExit(ExecutorService executorService, int seconds, boolean shutdownExecutor) {
        this.executorService = executorService;
        this.seconds = seconds;
        this.shutdownExecutor = shutdownExecutor;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(seconds);

            if (shutdownExecutor) {
                executorService.shutdown();
            }

            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}