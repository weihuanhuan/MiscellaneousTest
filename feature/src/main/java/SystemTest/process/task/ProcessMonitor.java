package SystemTest.process.task;

import java.util.concurrent.TimeUnit;

public class ProcessMonitor implements Runnable {

    private final Process process;

    public ProcessMonitor(Process process) {
        this.process = process;
    }

    @Override
    public void run() {
        while (true) {
            boolean alive = process.isAlive();
            long currentTimeMillis = System.currentTimeMillis();
            System.out.println("process.isAlive()=" + alive + ", System.currentTimeMillis()=" + currentTimeMillis);
            if (!alive) {
                break;
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

