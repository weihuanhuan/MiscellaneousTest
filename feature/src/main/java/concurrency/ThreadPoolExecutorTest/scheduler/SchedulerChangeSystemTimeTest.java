package concurrency.ThreadPoolExecutorTest.scheduler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerChangeSystemTimeTest {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * @see java.util.Timer
     */
    public static void main(String[] args) throws InterruptedException {

        //修改系统时间不能影响 java.util.concurrent.ScheduledExecutorService 的调度时间管理
        //因为其内部队列使用 java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.take 中
        // 使用 java.util.concurrent.locks.Condition.awaitNanos 通过 java.lang.System.nanoTime 来计算调度任务的时间。
        //另外注意 java.util.Timer 的调度则会受到系统时间的影响，
        // 因为他内部的 java.util.TimerThread.mainLoop 使用 java.lang.System.currentTimeMillis 来计算调度任务的时间。
        int intervalInSecond = 3;
        int initInSecond = 1;
        int delayInSecond = 10;
        int plusInSecond = 5;

        Thread thread = new Thread(new EchoTime(intervalInSecond));
        thread.start();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Server server = new Server(countDownLatch);
        scheduler.scheduleWithFixedDelay(server, initInSecond, delayInSecond, TimeUnit.SECONDS);
        countDownLatch.await();

        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        Date oldDate = new Date();
        String format = sdf.format(oldDate);
        System.out.println("old: " + format);

        Instant plusSeconds = oldDate.toInstant().plusSeconds(plusInSecond);
        Date newDate = Date.from(plusSeconds);
        String newFormat = sdf.format(newDate);
        System.out.println("set: " + newFormat);

        try {
            String[] s = newFormat.split(" ");
            String command = String.format("cmd /c time %s", s[1]);
            System.out.println("command:" + command);
            Process exec = Runtime.getRuntime().exec(command);
            int i = exec.waitFor();
            System.out.println(i);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("new: " + getTimeString());
    }

    public static class Server implements Runnable {

        private final CountDownLatch countDownLatch;

        public Server(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            System.out.println("time: " + getTimeString());
            countDownLatch.countDown();
        }
    }

    public static class EchoTime implements Runnable {

        private final int intervalInSecond;

        public EchoTime(int intervalInSecond) {
            this.intervalInSecond = intervalInSecond;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println("current: " + getTimeString());
                try {
                    TimeUnit.SECONDS.sleep(intervalInSecond);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        return sdf.format(new Date());
    }

}

