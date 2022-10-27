package concurrency.ThreadPoolExecutorTest.scheduler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SchedulerTest {

    public static void main(String[] args) throws InterruptedException {

        int kexTimeoutInSecond = 10;
        int stopTimeoutInSecond = 5;
        int adminTimeoutInSecond = 3;

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Server server = new Server(countDownLatch);

        //administrator connectSSH
        Runnable connectSSH = () -> server.connectSSH(kexTimeoutInSecond);
        Thread connectSSHThread = new Thread(connectSSH, "server-thread-connectSSH");
        connectSSHThread.start();
        System.out.println("connectSSHThread.start()");

        //administrator wait for connectSSH
        countDownLatch.await();

        //administrator calculate the duration time
        Runnable stopTimeoutService = () -> {
            long start = System.currentTimeMillis();
            server.stopTimeoutService(stopTimeoutInSecond, TimeUnit.SECONDS);
            long now = System.currentTimeMillis();

            long duration = now - start;
            System.out.println("now - start:" + duration);

            //administrator report timeout
            if (duration > adminTimeoutInSecond * 1000) {
                System.out.println("timeout");
            }
        };

        //administrator stopTimeoutService
        Thread stopTimeoutServiceThread = new Thread(stopTimeoutService, "server-thread-stopTimeoutService");
        stopTimeoutServiceThread.start();
        System.out.println("stopTimeoutServiceThread.start()");
    }

    public static class Server {

        CountDownLatch countDownLatch;

        public Server(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        public void connectSSH(int timeout) {
            TimeoutConnection connection = new TimeoutConnection();
            connection.connect(timeout * 1000);
            countDownLatch.countDown();
        }

        public void stopTimeoutService(int timeout, TimeUnit timeUnit) {
            TimeoutService.stop(timeout, timeUnit);
        }
    }

}

