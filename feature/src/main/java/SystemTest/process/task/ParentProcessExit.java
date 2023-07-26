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

            //这里需要考虑是否关闭线程池，否则在 SECONDS 后，但是由于线程池中还有线程在运行，导致启动该线程池的进行依旧无法退出
            if (shutdownExecutor) {
                executorService.shutdown();
            }

            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}